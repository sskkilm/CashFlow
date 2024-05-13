package com.sskkilm.cashflow.dto;

import com.sskkilm.cashflow.entity.Remittance;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RemittanceDto(
        String receivingAccountNumber,
        Integer remittanceAmount,
        Integer accountBalanceSnapshot,
        LocalDateTime createdAt
) {
    public static RemittanceDto fromEntity(Remittance remittance) {
        return RemittanceDto.builder()
                .receivingAccountNumber(remittance.getReceivingAccountNumber())
                .remittanceAmount(remittance.getAmount())
                .accountBalanceSnapshot(remittance.getAccountBalanceSnapshot())
                .createdAt(remittance.getCreatedAt())
                .build();
    }
}
