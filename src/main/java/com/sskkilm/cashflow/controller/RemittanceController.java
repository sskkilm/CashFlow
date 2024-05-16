package com.sskkilm.cashflow.controller;

import com.sskkilm.cashflow.dto.CreateRemittanceDto;
import com.sskkilm.cashflow.dto.RemittanceDto;
import com.sskkilm.cashflow.entity.User;
import com.sskkilm.cashflow.service.RemittanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

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
    public Slice<RemittanceDto> getRemittanceList(
            @PageableDefault(
                    size = 30, sort = "createdAt", direction = Sort.Direction.DESC
            ) Pageable pageable,
            @PathVariable Long accountId,
            @AuthenticationPrincipal User user
    ) {
        return remittanceService.getRemittanceList(pageable, accountId, user);
    }

    @GetMapping(value = "/remittances/{accountId}", params = {"startDate", "endDate"})
    public Page<RemittanceDto> getRemittanceList(
            @PageableDefault(
                    size = 30, sort = "createdAt", direction = Sort.Direction.DESC
            ) Pageable pageable,
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal User user
    ) {
        return remittanceService.getRemittanceList(
                pageable, accountId, user, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)
        );
    }
}
