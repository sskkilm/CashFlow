package com.sskkilm.cashflow.dto;

import com.sskkilm.cashflow.entity.Remittance;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

public class CreateRemittanceDto {
    public record Request(
            @NotNull
            @Min(1)
            Long accountId,
            @NotBlank(message = "계좌번호는 공백일 수 없습니다.")
            String receivingAccountNumber,
            @NotNull
            @Min(0)
            Integer remittanceAmount
    ) {

    }

    @Builder
    public record Response(
            String receivingAccountNumber,
            Integer remittanceAmount,
            Integer accountBalanceSnapshot,
            LocalDateTime createdAt
    ) {
        public static Response fromEntity(Remittance remittance) {
            return Response.builder()
                    .receivingAccountNumber(remittance.getReceivingAccountNumber())
                    .remittanceAmount(remittance.getAmount())
                    .accountBalanceSnapshot(remittance.getAccountBalanceSnapshot())
                    .createdAt(remittance.getCreatedAt())
                    .build();
        }
    }
}
