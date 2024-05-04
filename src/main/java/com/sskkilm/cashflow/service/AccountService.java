package com.sskkilm.cashflow.service;

import com.sskkilm.cashflow.dto.CreateAccountDto;
import com.sskkilm.cashflow.entity.Account;
import com.sskkilm.cashflow.entity.User;
import com.sskkilm.cashflow.enums.AccountErrorCode;
import com.sskkilm.cashflow.enums.AccountStatus;
import com.sskkilm.cashflow.exception.CustomException;
import com.sskkilm.cashflow.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public CreateAccountDto.Response createAccount(CreateAccountDto.Request request, User user) {
        // 이미 존재하는 계좌일 경우 예외 처리
        if (accountRepository.existsByAccountNumber(request.accountNumber())) {
            throw new CustomException(AccountErrorCode.ACCOUNT_ALREADY_EXISTS);
        }

        // 한 유저가 생성한 계좌가 10개 이상일 경우 예외 처리
        List<Account> accountList = accountRepository.findByUser(user);
        if (accountList.size() >= 10) {
            throw new CustomException(AccountErrorCode.ACCOUNT_CREATION_LIMIT);
        }

        Account account = accountRepository.save(Account.builder()
                .accountNumber(request.accountNumber())
                .balance(request.initialBalance())
                .status(AccountStatus.ACTIVE)
                .user(user)
                .build());

        return CreateAccountDto.Response.fromEntity(account);
    }
}
