package org.app.vestiobackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.dto.CreateOfferRequest;
import org.app.vestiobackend.dto.OfferDto;
import org.app.vestiobackend.security.AuthRequired;
import org.app.vestiobackend.service.OfferService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/listings/{id}/offers")
@AuthRequired
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OfferDto createOffer(@PathVariable UUID id, @Valid @RequestBody CreateOfferRequest request) {
        return offerService.createOffer(id, request);
    }

    @DeleteMapping("/{offerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOffer(@PathVariable UUID id, @PathVariable UUID offerId) {
        offerService.deleteOffer(id, offerId);
    }

    @PutMapping("/{offerId}/accept")
    public OfferDto acceptOffer(@PathVariable UUID id, @PathVariable UUID offerId) {
        return offerService.acceptOffer(id, offerId);
    }
}
