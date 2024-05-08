package com.sskkilm.cashflow.dto;

import com.sskkilm.cashflow.entity.Account;
import com.sskkilm.cashflow.enums.AccountStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

public class CreateAccountDto {
    public record Request(
            @NotBlank(message = "계좌번호는 공백일 수 없습니다.")
            String accountNumber,
            @Min(0)
            Integer initialBalance
    ) {
    }

    @Builder
    public record Response(
            Long accountId,
            String accountNumber,
            Integer balance,
            AccountStatus status,
            Long userId,
            LocalDateTime createdAt
    ) {
        public static Response fromEntity(Account account) {
            return Response.builder()
                    .accountId(account.getId())
                    .accountNumber(account.getAccountNumber())
                    .balance(account.getBalance())
                    .status(account.getStatus())
                    .userId(account.getUser().getId())
                    .createdAt(account.getCreatedAt())
                    .build();
        }
    }
}
