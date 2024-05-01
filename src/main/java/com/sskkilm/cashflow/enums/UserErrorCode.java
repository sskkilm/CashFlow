package com.sskkilm.cashflow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    ALREADY_EXIST_USER(400, "이미 존재하는 사용자입니다."),

    LOGIN_FAILED(401, "로그인 정보가 유효하지 않습니다."),
    USER_NOT_FOUND(400, "존재하지 않는 사용자입니다."),
    ;


    private final Integer status;
    private final String message;
}
