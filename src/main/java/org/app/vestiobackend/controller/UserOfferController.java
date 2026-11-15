package org.app.vestiobackend.controller;

import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.dto.OfferSummaryDto;
import org.app.vestiobackend.security.AuthRequired;
import org.app.vestiobackend.service.OfferService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AuthRequired
@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class UserOfferController {
    private final OfferService offerService;

    @GetMapping("/sent")
    public List<OfferSummaryDto> getSentOffers() {
        return offerService.getSentOffers();
    }

    @GetMapping("/received")
    public List<OfferSummaryDto> getReceivedOffers() {
        return offerService.getReceivedOffers();
    }
}
