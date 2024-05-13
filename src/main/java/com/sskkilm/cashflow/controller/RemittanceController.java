package com.sskkilm.cashflow.controller;

import com.sskkilm.cashflow.dto.CreateRemittanceDto;
import com.sskkilm.cashflow.dto.RemittanceDto;
import com.sskkilm.cashflow.entity.User;
import com.sskkilm.cashflow.service.RemittanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RemittanceController {

    private final RemittanceService remittanceService;

    @PostMapping("/remittances")
    public CreateRemittanceDto.Response createRemittance(
            @RequestBody @Valid CreateRemittanceDto.Request request,
            @AuthenticationPrincipal User user
    ) {
        return remittanceService.createRemittance(request, user);
    }

    @GetMapping("/remittances/{accountId}")
    public List<RemittanceDto> getRemittanceList(
            @PathVariable Long accountId,
            @AuthenticationPrincipal User user
    ) {
        return remittanceService.getRemittanceList(accountId, user);
    }

    @GetMapping(value = "/remittances/{accountId}", params = {"startDate", "endDate"})
    public List<RemittanceDto> getRemittanceList(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal User user
    ) {
        return remittanceService.getRemittanceList(
                accountId, user, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)
        );
    }
}
