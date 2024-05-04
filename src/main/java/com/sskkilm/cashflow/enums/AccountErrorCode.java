package com.sskkilm.cashflow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AccountErrorCode implements ErrorCode {

    ACCOUNT_CREATION_LIMIT(HttpStatus.FORBIDDEN, "계좌는 최대 10개까지 생성할 수 있습니다."),
    ACCOUNT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 계좌입니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
