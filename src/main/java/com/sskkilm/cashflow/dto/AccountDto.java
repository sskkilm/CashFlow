package com.sskkilm.cashflow.dto;

import com.sskkilm.cashflow.entity.Account;
import com.sskkilm.cashflow.enums.AccountStatus;
import lombok.Builder;

@Builder
public record AccountDto(
        Long accountId,
        String accountNumber,
        Integer balance,
        AccountStatus status
) {
    public static AccountDto fromEntity(Account account) {
        return AccountDto.builder()
                .accountId(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .status(account.getStatus())
                .build();
    }
}
