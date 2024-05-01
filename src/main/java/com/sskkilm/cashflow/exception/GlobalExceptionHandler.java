package com.sskkilm.cashflow.exception;

import com.sskkilm.cashflow.dto.ErrorResponse;
import com.sskkilm.cashflow.enums.GlobalErrorCode;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ErrorResponse handleCustomException(CustomException e) {
        return new ErrorResponse(e.getErrorCode().getStatus(), e.getErrorCode(), e.getErrorCode().getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ErrorResponse(400, GlobalErrorCode.INVALID_REQUEST, GlobalErrorCode.INVALID_REQUEST.getMessage());
    }

}
