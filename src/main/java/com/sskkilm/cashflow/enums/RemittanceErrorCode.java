package com.sskkilm.cashflow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RemittanceErrorCode implements ErrorCode {

    REMITTANCE_AND_RECEIVING_ACCOUNT_SAME(HttpStatus.BAD_REQUEST, "송금 계좌와 수금 계좌가 동일합니다."),
    RECEIVING_ACCOUNT_CAN_NOT_USE(HttpStatus.BAD_REQUEST, "수금 계좌가 비활성 상태입니다."),
    REMITTANCE_HISTORY_INQUIRY_PERIOD_LIMITED(HttpStatus.BAD_REQUEST, "송금이력 조회는 시작일부터 최대 1년까지 가능합니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
