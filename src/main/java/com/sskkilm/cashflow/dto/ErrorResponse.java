package com.sskkilm.cashflow.dto;

import com.sskkilm.cashflow.enums.ErrorCode;

public record ErrorResponse (
        Integer status,
        ErrorCode errorCode,
        String message
) {

}
