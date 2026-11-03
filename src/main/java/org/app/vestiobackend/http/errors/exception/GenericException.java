package org.app.vestiobackend.http.errors.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.app.vestiobackend.http.errors.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public abstract class GenericException extends RuntimeException{

    protected ErrorCode errorCode;
    protected HttpStatus httpStatus;

    public GenericException(){
        super();
    }

    public GenericException(String message) {
        super(message);
    }

    public GenericException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public GenericException(ErrorCode errorCode, String message, Throwable e){
        super(message, e);
    }

    abstract public HttpStatus getHttpStatus();

}
