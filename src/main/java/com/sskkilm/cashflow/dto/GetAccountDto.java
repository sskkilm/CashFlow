package com.sskkilm.cashflow.dto;

import com.sskkilm.cashflow.entity.Account;
import com.sskkilm.cashflow.enums.AccountStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GetAccountDto(
        Long accountId,
        String accountNumber,
        Integer balance,
        AccountStatus status,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static GetAccountDto fromEntity(Account account) {
        return GetAccountDto.builder()
                .accountId(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .modifiedAt(account.getModifiedAt())
                .build();
    }
}
