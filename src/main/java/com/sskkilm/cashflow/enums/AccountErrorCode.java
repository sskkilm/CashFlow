package com.sskkilm.cashflow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AccountErrorCode implements ErrorCode {

    ACCOUNT_CREATION_LIMIT(HttpStatus.FORBIDDEN, "계좌는 최대 10개까지 생성할 수 있습니다."),
    ACCOUNT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 계좌입니다."),
    ACCOUNT_ALREADY_INACTIVE(HttpStatus.BAD_REQUEST, "이미 해지된 계좌입니다."),
    ACCOUNT_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 계좌입니다."),
    ACCOUNT_USER_UN_MATCH(HttpStatus.BAD_REQUEST, "계좌와 소유주가 다릅니다."),
    ACCOUNT_CAN_NOT_USE(HttpStatus.BAD_REQUEST, "사용할 수 없는 계좌입니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
