package com.sskkilm.cashflow.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class DepositDto {
    public record Request(
            @NotNull
            @Min(1)
            Long accountId,
            @NotNull
            @Min(0)
            Integer depositAmount
    ) {

    }

    @Builder
    public record Response(
            Long accountId,
            Integer balanceBeforeDeposit,
            Integer depositAmount,
            Integer balanceAfterDeposit
    ) {

    }
}
