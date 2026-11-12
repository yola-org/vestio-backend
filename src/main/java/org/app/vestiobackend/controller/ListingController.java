package org.app.vestiobackend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.dto.ListingDto;
import org.app.vestiobackend.security.AuthRequired;
import org.app.vestiobackend.service.ListingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing product listings.
 * Provides endpoints for creating, retrieving, updating, and deleting listings.
 */
@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;

    /**
     * Endpoint to create a new listing.
     *
     * @param dto The listing data transfer object.
     * @return A response entity containing the created listing DTO.
     */
    @AuthRequired
    @PostMapping
    public ResponseEntity<ListingDto> createListing(@Valid @RequestBody ListingDto dto) {
        return ResponseEntity.ok(listingService.createListing(dto));
    }

    @AuthRequired
    @PostMapping(path = "/{id}/assets", consumes = "multipart/form-data")
    public ResponseEntity<ListingDto> uploadListingAssets(@PathVariable UUID id,
                                                           @RequestParam("images") @Size(min = 1, message = "At least one non-empty image is required") List<MultipartFile> images) {
        return ResponseEntity.ok(listingService.uploadAssets(id, images));
    }

    @AuthRequired
    @PutMapping(path = "/{id}/assets", consumes = "multipart/form-data")
    public ResponseEntity<ListingDto> replaceListingAssets(@PathVariable UUID id,
                                                          @RequestParam("images") @Size(min = 1, message = "At least one non-empty image is required") List<MultipartFile> images) {
        return ResponseEntity.ok(listingService.replaceAssets(id, images));
    }

    /**
     * Endpoint to retrieve a specific listing by ID.
     *
     * @param id The UUID of the listing.
     * @return A response entity containing the listing DTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ListingDto> getListing(@PathVariable UUID id) {
        return ResponseEntity.ok(listingService.getListing(id));
    }

    /**
     * Endpoint to retrieve all listings.
     *
     * @return A response entity containing a list of listing DTOs.
     */
    @GetMapping
    public ResponseEntity<List<ListingDto>> getAllListings() {
        return ResponseEntity.ok(listingService.getAllListings());
    }

    /**
     * Endpoint to update an existing listing.
     *
     * @param id  The UUID of the listing to update.
     * @param dto The updated listing data.
     * @return A response entity containing the updated listing DTO.
     */
    @AuthRequired
    @PutMapping("/{id}")
    public ResponseEntity<ListingDto> updateListing(@PathVariable UUID id, @Valid @RequestBody ListingDto dto) {
        return ResponseEntity.ok(listingService.updateListing(id, dto));
    }

    /**
     * Endpoint to delete a listing.
     *
     * @param id The UUID of the listing to delete.
     * @return A response entity with no content.
     */
    @AuthRequired
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListing(@PathVariable UUID id) {
        listingService.deleteListing(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Saves a listing to the authenticated user's favorites.
     *
     * @param id The UUID of the listing.
     * @return A 200 OK response, including when the listing is already saved.
     */
    @AuthRequired
    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void> addFavorite(@PathVariable UUID id) {
        listingService.addFavorite(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Removes a listing from the authenticated user's favorites.
     *
     * @param id The UUID of the listing.
     * @return A 200 OK response, including when an existing listing was never saved.
     */
    @AuthRequired
    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<Void> removeFavorite(@PathVariable UUID id) {
        listingService.removeFavorite(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves the authenticated user's favorited listings, newest favorite first.
     *
     * @return A response containing the saved listings.
     */
    @AuthRequired
    @GetMapping("/favorites")
    public ResponseEntity<List<ListingDto>> getFavorites() {
        return ResponseEntity.ok(listingService.getFavorites());
    }

    @AuthRequired
    @GetMapping("/pending")
    public ResponseEntity<List<ListingDto>> getPendingListingsForUser(
            @RequestParam(defaultValue = "12") @Min(1) int size,
            @RequestParam(defaultValue = "0") @Min(0) int page) {
        return ResponseEntity.ok(listingService.getPendingListingsForUser(size, page));
    }

}
