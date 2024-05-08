package com.sskkilm.cashflow.controller;

import com.sskkilm.cashflow.dto.*;
import com.sskkilm.cashflow.entity.User;
import com.sskkilm.cashflow.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/accounts")
    public CreateAccountDto.Response createAccount(
            @RequestBody @Valid CreateAccountDto.Request request,
            @AuthenticationPrincipal User user
    ) {
        return accountService.createAccount(request, user);
    }

    @PatchMapping("/accounts/{accountId}")
    public InactiveAccountDto.Response inactiveAccount(
            @PathVariable Long accountId,
            @AuthenticationPrincipal User user
    ) {
        return accountService.inactiveAccount(accountId, user);
    }

    @DeleteMapping("/accounts/{accountId}")
    public DeleteAccountDto.Response deleteAccount(
            @PathVariable Long accountId,
            @AuthenticationPrincipal User user
    ) {
        return accountService.deleteAccount(accountId, user);
    }

    @GetMapping("/accounts")
    public List<AccountDto> getTotalAccountList(
            @AuthenticationPrincipal User user
    ) {
        return accountService.getTotalAccountList(user);
    }

    @GetMapping("/accounts/active")
    public List<AccountDto> getActiveAccountList(
            @AuthenticationPrincipal User user
    ) {
        return accountService.getActiveAccountList(user);
    }

    @GetMapping("/accounts/inactive")
    public List<AccountDto> getInactiveAccountList(
            @AuthenticationPrincipal User user
    ) {
        return accountService.getInactiveAccountList(user);
    }

    @GetMapping("/accounts/{accountId}")
    public GetAccountDto getAccount(
            @PathVariable Long accountId,
            @AuthenticationPrincipal User user
    ) {
        return accountService.getAccount(accountId, user);
    }

    @PatchMapping("/accounts/deposit")
    public DepositDto.Response deposit(
            @RequestBody @Valid DepositDto.Request request,
            @AuthenticationPrincipal User user
    ) {
        return accountService.deposit(request, user);
    }

    @PatchMapping("/accounts/withdraw")
    public WithdrawDto.Response withdraw(
            @RequestBody @Valid WithdrawDto.Request request,
            @AuthenticationPrincipal User user
    ) {
        return accountService.withdraw(request, user);
    }
}
