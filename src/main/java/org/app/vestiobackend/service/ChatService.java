package org.app.vestiobackend.service;

import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.dto.ChatDto;
import org.app.vestiobackend.dto.MessageDto;
import org.app.vestiobackend.dto.PaginatedResult;
import org.app.vestiobackend.mapper.PaginationMapper;
import org.app.vestiobackend.dto.SendMessageRequest;
import org.app.vestiobackend.entity.Chat;
import org.app.vestiobackend.entity.Message;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.http.errors.ErrorCode;
import org.app.vestiobackend.http.errors.exception.BadRequestException;
import org.app.vestiobackend.http.errors.exception.ResourceNotFoundException;
import org.app.vestiobackend.repository.ChatRepository;
import org.app.vestiobackend.repository.MessageRepository;
import org.app.vestiobackend.security.UserContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserContext userContext;
    private final ChatMessageEvents chatMessageEvents;

    @Transactional(readOnly = true)
    public PaginatedResult<ChatDto> list(int page, int size) {
        UUID userId = userContext.getCurrentUser().getId();
        return PaginationMapper.from(chatRepository.findForParticipant(userId, PageRequest.of(page, size))
                .map(summary -> ChatDto.from(summary.getChat(), summary.getLastMessage(), summary.getListingCoverAssetKey())));
    }

    @Transactional(readOnly = true)
    public ChatDto get(UUID id) {
        ChatRepository.ChatSummary summary = chatRepository.findSummaryForParticipant(id, userContext.getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "Chat not found"));
        return ChatDto.from(summary.getChat(), summary.getLastMessage(), summary.getListingCoverAssetKey());
    }

    @Transactional(readOnly = true)
    public PaginatedResult<MessageDto> messages(UUID id, int page, int size) {
        Chat chat = chatRepository.findByIdForParticipant(id, userContext.getCurrentUser().getId()).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "Chat not found"));
        return PaginationMapper.from(messageRepository.findByChat_Id(chat.getId(), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id"))).map(MessageDto::from));
    }

    @Transactional
    public MessageDto send(UUID id, SendMessageRequest request) {
        User sender = userContext.getCurrentUser();
        Chat chat = chatRepository.findForMessage(id, sender.getId()).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "Chat not found"));
        if (request == null || request.getContent() == null || request.getContent().isBlank()
                || request.getContent().length() > Message.MAX_CONTENT_LENGTH) {
            throw new BadRequestException(ErrorCode.ERR_INVALID_MESSAGE, "Message content must contain 1 to 4000 characters");
        }
        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(request.getContent());
        message.setSystem(false);
        Message saved = messageRepository.saveAndFlush(message);
        chatMessageEvents.publish(saved);
        return MessageDto.from(saved);
    }
}
