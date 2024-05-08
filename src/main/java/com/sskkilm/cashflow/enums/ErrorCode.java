package com.sskkilm.cashflow.enums;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    public HttpStatus getStatus();

    public String getMessage();
}