package com.sskkilm.cashflow.controller;

import com.sskkilm.cashflow.dto.*;
import com.sskkilm.cashflow.entity.User;
import com.sskkilm.cashflow.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/accounts")
    public CreateAccountDto.Response createAccount(
            @RequestBody @Valid CreateAccountDto.Request request
    ) {
        // 로그인 중인 사용자 정보 불러오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return accountService.createAccount(request, user);
    }

    @PatchMapping("/accounts/{accountId}")
    public InactiveAccountDto.Response inactiveAccount(
            @PathVariable Long accountId
    ) {
        // 로그인 중인 사용자 정보 불러오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return accountService.inactiveAccount(accountId, user);
    }

    @DeleteMapping("/accounts/{accountId}")
    public DeleteAccountDto.Response deleteAccount(
            @PathVariable Long accountId
    ) {
        // 로그인 중인 사용자 정보 불러오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return accountService.deleteAccount(accountId, user);
    }

    @GetMapping("/accounts")
    public List<AccountDto> getTotalAccountList() {
        // 로그인 중인 사용자 정보 불러오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return accountService.getTotalAccountList(user);
    }

    @GetMapping("/accounts/active")
    public List<AccountDto> getActiveAccountList() {
        // 로그인 중인 사용자 정보 불러오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return accountService.getActiveAccountList(user);
    }

    @GetMapping("/accounts/inactive")
    public List<AccountDto> getInactiveAccountList() {
        // 로그인 중인 사용자 정보 불러오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return accountService.getInactiveAccountList(user);
    }

    @GetMapping("/accounts/{accountId}")
    public GetAccountDto getAccount(
            @PathVariable Long accountId
    ) {
        // 로그인 중인 사용자 정보 불러오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return accountService.getAccount(accountId, user);
    }
}
