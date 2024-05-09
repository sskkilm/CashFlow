package com.sskkilm.cashflow.controller;

import com.sskkilm.cashflow.dto.CreateRemittanceDto;
import com.sskkilm.cashflow.entity.User;
import com.sskkilm.cashflow.service.RemittanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
