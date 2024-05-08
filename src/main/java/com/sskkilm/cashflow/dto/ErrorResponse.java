package com.sskkilm.cashflow.dto;

import com.sskkilm.cashflow.enums.ErrorCode;

public record ErrorResponse(
        ErrorCode errorCode,
        String message
) {

}
