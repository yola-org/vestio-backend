package org.app.vestiobackend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.dto.ListingDto;
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
    @PostMapping
    public ResponseEntity<ListingDto> createListing(@Valid @RequestBody ListingDto dto) {
        return ResponseEntity.ok(listingService.createListing(dto));
    }

    @PostMapping(path = "/{id}/assets", consumes = "multipart/form-data")
    public ResponseEntity<ListingDto> uploadListingAssets(@PathVariable UUID id,
                                                           @RequestParam("images") @Size(min = 1, message = "At least one non-empty image is required") List<MultipartFile> images) {
        return ResponseEntity.ok(listingService.uploadAssets(id, images));
    }

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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListing(@PathVariable UUID id) {
        listingService.deleteListing(id);
        return ResponseEntity.noContent().build();
    }
}
