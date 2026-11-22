package org.app.vestiobackend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.dto.ChatDto;
import org.app.vestiobackend.dto.MessageDto;
import org.app.vestiobackend.dto.PaginatedResult;
import org.app.vestiobackend.dto.SendMessageRequest;
import org.app.vestiobackend.security.AuthRequired;
import org.app.vestiobackend.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AuthRequired
@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping
    public PaginatedResult<ChatDto> list(@RequestParam(defaultValue = "0") @Min(0) int page,
                                         @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return chatService.list(page, size);
    }

    @GetMapping("/{id}")
    public ChatDto get(@PathVariable UUID id) {
        return chatService.get(id);
    }

    @GetMapping("/{id}/messages")
    public PaginatedResult<MessageDto> messages(@PathVariable UUID id,
                                                @RequestParam(defaultValue = "0") @Min(0) int page,
                                                @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return chatService.messages(id, page, size);
    }

    @PostMapping("/{id}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDto send(@PathVariable UUID id, @Valid @RequestBody SendMessageRequest request) {
        return chatService.send(id, request);
    }
}
