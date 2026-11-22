package org.app.vestiobackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.app.vestiobackend.entity.Message;

@Getter
@Setter
public class SendMessageRequest {
    @NotBlank
    @Size(max = Message.MAX_CONTENT_LENGTH)
    private String content;
}
