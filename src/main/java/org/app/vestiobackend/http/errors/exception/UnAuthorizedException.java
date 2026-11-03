package org.app.vestiobackend.http.errors.exception;

import org.app.vestiobackend.http.errors.ErrorCode;
import org.springframework.http.HttpStatus;

public class UnAuthorizedException extends GenericException{

    public UnAuthorizedException(){
        super();
        this.errorCode = ErrorCode.ERR_UNAUTHORIZED;
    }

    public UnAuthorizedException(String message) {
        super(ErrorCode.ERR_UNAUTHORIZED, message);
    }

    public UnAuthorizedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

}
