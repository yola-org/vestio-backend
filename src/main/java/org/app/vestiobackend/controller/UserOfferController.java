package org.app.vestiobackend.controller;

import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.dto.OfferSummaryDto;
import org.app.vestiobackend.security.AuthRequired;
import org.app.vestiobackend.service.OfferService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import org.app.vestiobackend.dto.PaginatedResult;
import org.springframework.web.bind.annotation.RequestParam;

@AuthRequired
@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class UserOfferController {
    private final OfferService offerService;

    @GetMapping("/sent")
    public PaginatedResult<OfferSummaryDto> getSentOffers(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "12") @Min(1) @Max(100) int size) {
        return offerService.getSentOffers(page, size);
    }

    @GetMapping("/received")
    public PaginatedResult<OfferSummaryDto> getReceivedOffers(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "12") @Min(1) @Max(100) int size) {
        return offerService.getReceivedOffers(page, size);
    }
}
