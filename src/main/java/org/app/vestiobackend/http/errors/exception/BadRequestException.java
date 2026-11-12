package org.app.vestiobackend.http.errors.exception;

import org.app.vestiobackend.http.errors.ErrorCode;
import org.springframework.http.HttpStatus;

public class BadRequestException extends GenericException {

    public BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
