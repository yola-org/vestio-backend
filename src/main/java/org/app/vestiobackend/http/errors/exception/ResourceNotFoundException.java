package org.app.vestiobackend.http.errors.exception;

import org.app.vestiobackend.http.errors.ErrorCode;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends GenericException {

    public ResourceNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
