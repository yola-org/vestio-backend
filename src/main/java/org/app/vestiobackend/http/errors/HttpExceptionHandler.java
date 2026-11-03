package org.app.vestiobackend.http.errors;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.app.vestiobackend.http.errors.exception.GenericException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class HttpExceptionHandler{

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(GenericException ex, HttpServletRequest request){
        if (ex.getHttpStatus() == HttpStatus.INTERNAL_SERVER_ERROR) {
            log.error("Internal Server Error: {}", ex.getMessage(), ex);
        }

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(ex.getHttpStatus().value())
                .error(ex.getErrorCode().name())
                .message(ex.getHttpStatus() == HttpStatus.INTERNAL_SERVER_ERROR ? "Internal Server Error" : ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<ErrorResponse.FieldValidationError> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> new ErrorResponse.FieldValidationError(error.getField(), error.getDefaultMessage(), error.getRejectedValue()))
                .toList();

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_FAILED")
                .message("One or more validation errors occurred")
                .path(request.getRequestURI())
                .validationErrors(errors)
                .build();

        return ResponseEntity
                .badRequest()
                .body(response);
    }

}
