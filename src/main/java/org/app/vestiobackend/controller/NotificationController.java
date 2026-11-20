package org.app.vestiobackend.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.dto.NotificationDto;
import org.app.vestiobackend.dto.NotificationPageDto;
import org.app.vestiobackend.dto.NotificationUnreadCountDto;
import org.app.vestiobackend.security.AuthRequired;
import org.app.vestiobackend.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@AuthRequired
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public NotificationPageDto list(@RequestParam(defaultValue = "0") @Min(0) int page,
                                    @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
                                    @RequestParam(defaultValue = "false") boolean unreadOnly) {
        return notificationService.list(page, size, unreadOnly);
    }

    @GetMapping("/unread-count")
    public NotificationUnreadCountDto unreadCount() {
        return new NotificationUnreadCountDto(notificationService.unreadCount());
    }

    @PutMapping("/{id}/read")
    public NotificationDto markRead(@PathVariable UUID id) {
        return notificationService.markRead(id);
    }

    @PutMapping("/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllRead() {
        notificationService.markAllRead();
    }
}
