package com.sskkilm.cashflow.dto;

import com.sskkilm.cashflow.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public record ErrorResponse (
        Integer status,
        ErrorCode errorCode,
        String message
) {

}
