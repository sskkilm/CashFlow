package com.sskkilm.cashflow.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class WithdrawDto {
    public record Request(
            @NotNull
            @Min(1)
            Long accountId,
            @NotNull
            @Min(0)
            Integer withdrawAmount
    ) {

    }

    @Builder
    public record Response(
            Long accountId,
            Integer balanceBeforeWithdraw,
            Integer withdrawAmount,
            Integer balanceAfterWithdraw
    ) {

    }
}
