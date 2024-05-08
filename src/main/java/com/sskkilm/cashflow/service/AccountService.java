package com.sskkilm.cashflow.service;

import com.sskkilm.cashflow.dto.*;
import com.sskkilm.cashflow.entity.Account;
import com.sskkilm.cashflow.entity.User;
import com.sskkilm.cashflow.enums.AccountErrorCode;
import com.sskkilm.cashflow.enums.AccountStatus;
import com.sskkilm.cashflow.exception.CustomException;
import com.sskkilm.cashflow.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private static final int MAXIMUM_NUMBER_OF_ACCOUNTS = 10;

    public CreateAccountDto.Response createAccount(CreateAccountDto.Request request, User user) {
        // 이미 존재하는 계좌일 경우 예외 처리
        if (accountRepository.existsByAccountNumber(request.accountNumber())) {
            throw new CustomException(AccountErrorCode.ACCOUNT_ALREADY_EXISTS);
        }

        // 한 유저가 생성한 계좌가 10개 이상일 경우 예외 처리
        List<Account> accountList = accountRepository.findByUser(user);
        if (accountList.size() >= MAXIMUM_NUMBER_OF_ACCOUNTS) {
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

    @Transactional
    public InactiveAccountDto.Response inactiveAccount(Long accountId, User user) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new CustomException(AccountErrorCode.ACCOUNT_NOT_FOUND));
        if (!Objects.equals(account.getUser().getId(), user.getId())) {
            throw new CustomException(AccountErrorCode.ACCOUNT_USER_UN_MATCH);
        }
        if (account.getStatus() == AccountStatus.INACTIVE) {
            throw new CustomException(AccountErrorCode.ACCOUNT_ALREADY_INACTIVE);
        }

        account.inactive();

        return InactiveAccountDto.Response.fromEntity(account);
    }

    public DeleteAccountDto.Response deleteAccount(Long accountId, User user) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new CustomException(AccountErrorCode.ACCOUNT_NOT_FOUND));
        if (!Objects.equals(account.getUser().getId(), user.getId())) {
            throw new CustomException(AccountErrorCode.ACCOUNT_USER_UN_MATCH);
        }

        accountRepository.delete(account);

        return DeleteAccountDto.Response.fromEntity(account);
    }

    public List<AccountDto> getTotalAccountList(User user) {
        List<Account> accountList = accountRepository.findAllByUserOrderByCreatedAt(user);

        return accountList.stream().map(AccountDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AccountDto> getActiveAccountList(User user) {
        List<Account> accountList = accountRepository.findAllByUserAndStatusOrderByCreatedAt(user, AccountStatus.ACTIVE);

        return accountList.stream().map(AccountDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AccountDto> getInactiveAccountList(User user) {
        List<Account> accountList = accountRepository.findAllByUserAndStatusOrderByCreatedAt(user, AccountStatus.INACTIVE);

        return accountList.stream().map(AccountDto::fromEntity)
                .collect(Collectors.toList());
    }

    public GetAccountDto getAccount(Long accountId, User user) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new CustomException(AccountErrorCode.ACCOUNT_NOT_FOUND));
        if (!Objects.equals(account.getUser().getId(), user.getId())) {
            throw new CustomException(AccountErrorCode.ACCOUNT_USER_UN_MATCH);
        }

        return GetAccountDto.fromEntity(account);
    }
}
