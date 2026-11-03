package org.app.vestiobackend.http.errors.exception;

import org.app.vestiobackend.http.errors.ErrorCode;
import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends GenericException{

    public ResourceAlreadyExistsException(String message) {
        super(message);
        this.errorCode = ErrorCode.ERR_RESOURCE_ALREADY_EXISTS;
    }

    public ResourceAlreadyExistsException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}
