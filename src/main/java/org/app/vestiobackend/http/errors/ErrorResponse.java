package org.app.vestiobackend.http.errors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<FieldValidationError> validationErrors;

    public record FieldValidationError(
            String field,
            String message,
            Object rejectedValue
    ){}
}
