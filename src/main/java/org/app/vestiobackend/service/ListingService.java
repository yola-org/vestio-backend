package org.app.vestiobackend.service;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.dto.ListingDto;
import org.app.vestiobackend.dto.ListingSummaryDto;
import org.app.vestiobackend.dto.ListingSearchFilters;
import org.app.vestiobackend.entity.*;
import com.vestio.common.entity.enums.ListingStatus;
import org.app.vestiobackend.http.errors.ErrorCode;
import org.app.vestiobackend.http.errors.exception.BadRequestException;
import org.app.vestiobackend.http.errors.exception.ResourceNotFoundException;
import org.app.vestiobackend.http.errors.exception.UnAuthorizedException;
import org.app.vestiobackend.repository.CategoryRepository;
import org.app.vestiobackend.repository.CityRepository;
import org.app.vestiobackend.repository.FavoriteRepository;
import org.app.vestiobackend.repository.ListingRepository;
import org.app.vestiobackend.repository.ListingSummaryRepository;
import org.app.vestiobackend.repository.ListingSearchSpecification;
import org.app.vestiobackend.repository.SizeRepository;
import org.app.vestiobackend.service.AssetService.UploadedAsset;
import org.app.vestiobackend.security.UserContext;
import org.app.vestiobackend.mapper.ListingMapper;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.app.vestiobackend.dto.PaginatedResult;
import org.app.vestiobackend.mapper.PaginationMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service for managing product listings.
 * Handles creation, retrieval, updates, and deletion of listings, including authorization checks.
 */
@Service
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository listingRepository;
    private final CityRepository cityRepository;
    private final CategoryRepository categoryRepository;
    private final SizeRepository sizeRepository;
    private final ListingMapper listingMapper;
    private final UserContext userContext;
    private final AssetService assetService;
    private final FavoriteRepository favoriteRepository;
    private final OfferService offerService;
    private final EntityManagerFactory entityManagerFactory;
    private final NotificationService notificationService;
    private final ListingSummaryRepository listingSummaryRepository;

    /**
     * Creates a new listing for the currently authenticated user.
     *
     * @param dto The listing data transfer object.
     * @return The created listing as a {@link ListingDto}.
     * @throws ResourceNotFoundException if the specified city or category is not found.
     */
    @Transactional
    public ListingDto createListing(ListingDto dto) {
        User user = userContext.getCurrentUser();
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "Category not found"));

        Listing listing = listingMapper.toEntity(dto);
        listing.setId(UUID.randomUUID());
        listing.setUser(user);
        listing.setCategory(category);

        if (dto.getCityIds() != null && !dto.getCityIds().isEmpty()) {
            List<City> cities = cityRepository.findAllById(dto.getCityIds());
            if (cities.size() != dto.getCityIds().size()) {
                throw new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "One or more cities not found");
            }
            listing.setCities(new LinkedHashSet<>(cities));
        }

        if (dto.getSizeIds() != null && !dto.getSizeIds().isEmpty()) {
            List<Size> sizes = sizeRepository.findAllById(dto.getSizeIds());
            if (sizes.size() != dto.getSizeIds().size()) {
                throw new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "One or more sizes not found");
            }
            validateCategorySizes(listing.getCategory(), sizes);
            listing.setSizes(new LinkedHashSet<>(sizes));
        }

        // Listing status is controlled by the asset workflow, never by the request body.
        listing.setStatus(ListingStatus.AWAITING_ASSETS);

        Listing saved = listingRepository.save(listing);
        notificationService.notifyUserListingCreated(user.getId(), saved.getTitle(), saved.getId());
        return listingMapper.toDto(saved);
    }

    /**
     * Retrieves a specific listing by its unique identifier.
     *
     * @param id The UUID of the listing.
     * @return The listing as a {@link ListingDto}.
     * @throws ResourceNotFoundException if the listing is not found.
     */
    @Transactional(readOnly = true)
    public ListingDto getListing(UUID id) {
        Listing listing = listingRepository.findByIdAndStatus(id, ListingStatus.AVAILABLE)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "Listing not found"));
        return listingMapper.toDto(listing);
    }

    /** Retrieves a database-paginated page of available listings, newest first. */
    @Transactional(readOnly = true)
    public PaginatedResult<ListingSummaryDto> getAllListings(int size, int page) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
        return PaginationMapper.from(listingSummaryRepository.available(pageable));
    }

    @Transactional(readOnly = true)
    public PaginatedResult<ListingSummaryDto> searchListings(ListingSearchFilters filters, int size, int page) {
        Pageable pageable = PageRequest.of(page, size);
        boolean postgres = entityManagerFactory.unwrap(SessionFactoryImplementor.class)
                .getJdbcServices().getDialect() instanceof PostgreSQLDialect;
        Set<UUID> categoryIds = new HashSet<>();
        if (filters.categoryId() != null) {
            categoryIds.add(filters.categoryId());
            if (Boolean.TRUE.equals(filters.includeSubcategories())) {
                List<UUID> parents = List.of(filters.categoryId());
                while (!parents.isEmpty()) {
                    parents = categoryRepository.findChildIds(parents).stream()
                            .filter(categoryIds::add)
                            .toList();
                }
            }
        }
        return PaginationMapper.from(listingSummaryRepository.page(
                        ListingSearchSpecification.matching(filters, postgres, categoryIds), pageable));
    }

    /**
     * Updates an existing listing.
     * Verifies that the currently authenticated user is the owner of the listing.
     *
     * @param id  The UUID of the listing to update.
     * @param dto The updated listing data.
     * @return The updated listing as a {@link ListingDto}.
     * @throws ResourceNotFoundException if the listing is not found.
     * @throws UnAuthorizedException     if the current user is not the owner of the listing.
     */
    @Transactional
    public ListingDto updateListing(UUID id, ListingDto dto) {
        Listing listing = listingRepository.findForUpdateById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "Listing not found"));

        User currentUser = userContext.getCurrentUser();
        if (!listing.getUser().getId().equals(currentUser.getId())) {
            throw new UnAuthorizedException(ErrorCode.ERR_UNAUTHORIZED, "Unauthorized to update this listing");
        }

        offerService.validateListingContactTypes(listing, dto.getContactTypes());
        listingMapper.updateListingFromDto(dto, listing);

        if (dto.getCityIds() != null) {
            List<City> cities = cityRepository.findAllById(dto.getCityIds());
            if (cities.size() != dto.getCityIds().size()) {
                throw new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "One or more cities not found");
            }
            listing.setCities(new LinkedHashSet<>(cities));
        }

        if (dto.getCategoryId() != null && !dto.getCategoryId().equals(listing.getCategory().getId())) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "Category not found"));
            listing.setCategory(category);
        }

        if (dto.getSizeIds() != null) {
            List<Size> sizes = sizeRepository.findAllById(dto.getSizeIds());
            if (sizes.size() != dto.getSizeIds().size()) {
                throw new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "One or more sizes not found");
            }
            listing.setSizes(new LinkedHashSet<>(sizes));
        }

        validateCategorySizes(listing.getCategory(), new ArrayList<>(listing.getSizes() == null ? Set.of() : listing.getSizes()));
        return listingMapper.toDto(listingRepository.save(listing));
    }

    private void validateCategorySizes(Category category, List<Size> sizes) {
        if (sizes.isEmpty()) return;
        if (category.getSizeGroup() == null || sizes.stream().anyMatch(size ->
                !size.getSizeGroup().getId().equals(category.getSizeGroup().getId()))) {
            throw new BadRequestException(ErrorCode.ERR_INVALID_CATEGORY_SIZES, "Sizes must belong to the selected category");
        }
    }

    @Transactional
    public ListingDto uploadAssets(UUID id, List<MultipartFile> images) {
        return saveAssets(id, images, false);
    }

    @Transactional
    public ListingDto replaceAssets(UUID id, List<MultipartFile> images) {
        return saveAssets(id, images, true);
    }

    private ListingDto saveAssets(UUID id, List<MultipartFile> images, boolean replace) {
        Listing listing = listingRepository.findForUpdateById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "Listing not found"));

        User currentUser = userContext.getCurrentUser();
        if (!listing.getUser().getId().equals(currentUser.getId())) {
            throw new UnAuthorizedException(ErrorCode.ERR_UNAUTHORIZED, "Unauthorized to upload listing assets");
        }

        List<UploadedAsset> assets = assetService.uploadListingImages(listing.getId(), images);
        if (listing.getAssetKeys() == null) {
            listing.setAssetKeys(new ArrayList<>());
        }
        if (replace) {
            listing.getAssetKeys().clear();
        }
        listing.getAssetKeys().addAll(assets.stream().map(UploadedAsset::id).toList());
        listing.setStatus(ListingStatus.AWAITING_ASSETS);
        // Keep the row locked through SQS publication so the optimizer sees the committed assets.
        listingRepository.flush();
        ListingDto result = listingMapper.toDto(listing);
        assetService.queueListingImageOptimization(currentUser.getId(), listing.getId(), assets);
        return result;
    }

    /**
     * Deletes an existing listing.
     * Verifies that the currently authenticated user is the owner of the listing.
     *
     * @param id The UUID of the listing to delete.
     * @throws ResourceNotFoundException if the listing is not found.
     * @throws UnAuthorizedException     if the current user is not the owner of the listing.
     */
    @Transactional
    public void deleteListing(UUID id) {
        Listing listing = listingRepository.findForUpdateById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "Listing not found"));

        User currentUser = userContext.getCurrentUser();
        if (!listing.getUser().getId().equals(currentUser.getId())) {
            throw new UnAuthorizedException(ErrorCode.ERR_UNAUTHORIZED, "Unauthorized to delete this listing");
        }

        listingRepository.delete(listing);
    }

    /**
     * Adds a listing to the current user's favorites if it is not already saved.
     *
     * @param id The UUID of the listing.
     * @throws ResourceNotFoundException if the listing is not found.
     * @throws BadRequestException if the listing belongs to the current user.
     */
    @Transactional
    public void addFavorite(UUID id) {
        User user = userContext.getCurrentUser();
        Listing listing = getListingForFavoriteMutation(id, user);
        if (favoriteRepository.existsByUserAndListing(user, listing)) {
            return;
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setListing(listing);
        favoriteRepository.save(favorite);
    }

    /**
     * Removes a listing from the current user's favorites if it was saved.
     *
     * @param id The UUID of the listing.
     * @throws ResourceNotFoundException if the listing is not found.
     * @throws BadRequestException if the listing belongs to the current user.
     */
    @Transactional
    public void removeFavorite(UUID id) {
        User user = userContext.getCurrentUser();
        Listing listing = getListingForFavoriteMutation(id, user);
        favoriteRepository.deleteByUserAndListing(user, listing);
    }

    /**
     * Retrieves the current user's favorited listings in reverse chronological order.
     *
     * @return The saved listing summaries.
     */
    @Transactional(readOnly = true)
    public PaginatedResult<ListingSummaryDto> getFavorites(int size, int page, List<UUID> listingIds) {
        return PaginationMapper.from(listingSummaryRepository.favorites(userContext.getCurrentUser().getId(), listingIds, PageRequest.of(page, size)));
    }

    private Listing getListingForFavoriteMutation(UUID id, User user) {
        Listing listing = listingRepository.findForFavoriteMutationById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "Listing not found"));

        if (listing.getUser().getId().equals(user.getId())) {
            throw new BadRequestException(
                    ErrorCode.ERR_CANNOT_FAVORITE_OWN_LISTING,
                    "Cannot change the favorite status of your own listing");
        }
        return listing;
    }

    @Transactional(readOnly = true)
    public PaginatedResult<ListingSummaryDto> getPendingListingsForUser(int size, int page) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
        return PaginationMapper.from(listingSummaryRepository.forUser(userContext.getCurrentUser().getId(),
                List.of(ListingStatus.AWAITING_ASSETS, ListingStatus.PENDING, ListingStatus.REJECTED_CONTENT), pageable));
    }

    @Transactional(readOnly = true)
    public PaginatedResult<ListingSummaryDto> getListingsForCurrentUser(int size, int page, ListingStatus status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
        return PaginationMapper.from(listingSummaryRepository.forUser(userContext.getCurrentUser().getId(), status == null ? List.of() : List.of(status), pageable));
    }
}
