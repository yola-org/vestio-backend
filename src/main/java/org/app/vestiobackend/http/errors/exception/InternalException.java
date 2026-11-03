package org.app.vestiobackend.http.errors.exception;

import org.app.vestiobackend.http.errors.ErrorCode;
import org.springframework.http.HttpStatus;

public class InternalException extends GenericException{

    public InternalException(String message) {
        super(message);
        this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    }

    public InternalException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public InternalException(ErrorCode errorCode, String message, Throwable e){
        super(errorCode, message, e);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
