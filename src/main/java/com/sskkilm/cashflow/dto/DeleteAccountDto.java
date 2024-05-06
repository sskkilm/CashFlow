package com.sskkilm.cashflow.dto;

import com.sskkilm.cashflow.entity.Account;
import com.sskkilm.cashflow.enums.AccountStatus;
import lombok.Builder;

public class DeleteAccountDto {
    @Builder
    public record Response(
            Long id,
            String accountNumber,
            Integer balance,
            AccountStatus status,
            Long userId
    ) {
        public static Response fromEntity(Account account) {
            return Response.builder()
                    .id(account.getId())
                    .accountNumber(account.getAccountNumber())
                    .balance(account.getBalance())
                    .status(account.getStatus())
                    .userId(account.getUser().getId())
                    .build();
        }
    }
}
