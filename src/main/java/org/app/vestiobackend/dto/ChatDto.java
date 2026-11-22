package org.app.vestiobackend.dto;

import org.app.vestiobackend.entity.Chat;
import org.app.vestiobackend.entity.Message;
import org.app.vestiobackend.entity.User;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ChatDto(UUID id, UUID offerId, UUID listingId, String listingTitle, UUID listingCoverAssetKey,
                      UUID ownerId, String ownerName, UUID buyerId, String buyerName, String buyerProfileImageKey, Instant createdAt,
                      MessageDto lastMessage) {
    public static ChatDto from(Chat chat, Message lastMessage, UUID listingCoverAssetKey) {
        return new ChatDto(chat.getId(), chat.getOffer().getId(), chat.getListing().getId(),
                chat.getListing().getTitle(), listingCoverAssetKey, chat.getOwner().getId(), name(chat.getOwner()),
                chat.getBuyer().getId(), name(chat.getBuyer()), chat.getBuyer().getProfileImageKey(), chat.getCreatedAt(),
                lastMessage == null ? null : MessageDto.from(lastMessage));
    }

    private static String name(User user) {
        return Stream.of(user.getFirstName(), user.getLastName())
                .filter(part -> part != null && !part.isBlank()).map(String::strip)
                .collect(Collectors.joining(" "));
    }
}
