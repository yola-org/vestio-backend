package org.app.vestiobackend.dto;

import java.util.List;

public record NotificationPageDto(List<NotificationDto> items, int page, int size, long totalElements,
                                  int totalPages, long unreadCount) { }
