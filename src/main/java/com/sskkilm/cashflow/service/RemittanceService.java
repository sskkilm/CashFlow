package com.sskkilm.cashflow.service;

import com.sskkilm.cashflow.dto.CreateRemittanceDto;
import com.sskkilm.cashflow.dto.RemittanceDto;
import com.sskkilm.cashflow.entity.Account;
import com.sskkilm.cashflow.entity.Remittance;
import com.sskkilm.cashflow.entity.User;
import com.sskkilm.cashflow.enums.AccountErrorCode;
import com.sskkilm.cashflow.enums.AccountStatus;
import com.sskkilm.cashflow.enums.RemittanceErrorCode;
import com.sskkilm.cashflow.exception.CustomException;
import com.sskkilm.cashflow.repository.AccountRepository;
import com.sskkilm.cashflow.repository.RemittanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RemittanceService {

    private final AccountRepository accountRepository;
    private final RemittanceRepository remittanceRepository;

    @Transactional
    public CreateRemittanceDto.Response createRemittance(CreateRemittanceDto.Request request, User user) {
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new CustomException(AccountErrorCode.ACCOUNT_NOT_FOUND));
        if (!Objects.equals(account.getUser().getId(), user.getId())) {
            throw new CustomException(AccountErrorCode.ACCOUNT_USER_UN_MATCH);
        }
        if (account.getStatus() == AccountStatus.INACTIVE) {
            throw new CustomException(AccountErrorCode.ACCOUNT_CAN_NOT_USE);
        }
        if (account.getBalance() < request.remittanceAmount()) {
            throw new CustomException(AccountErrorCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }

        Account receivingAccount = accountRepository.findByAccountNumber(request.receivingAccountNumber())
                .orElseThrow(() -> new CustomException(AccountErrorCode.ACCOUNT_NOT_FOUND));
        if (receivingAccount.getStatus() == AccountStatus.INACTIVE) {
            throw new CustomException(RemittanceErrorCode.RECEIVING_ACCOUNT_CAN_NOT_USE);
        }

        if (Objects.equals(account.getId(), receivingAccount.getId())) {
            throw new CustomException(RemittanceErrorCode.REMITTANCE_AND_RECEIVING_ACCOUNT_SAME);
        }

        account.withdraw(request.remittanceAmount());
        receivingAccount.deposit(request.remittanceAmount());

        Remittance remittance = remittanceRepository.save(
                Remittance.builder()
                        .receivingAccountNumber(request.receivingAccountNumber())
                        .amount(request.remittanceAmount())
                        .accountBalanceSnapshot(account.getBalance())
                        .account(account)
                        .build()
        );

        return CreateRemittanceDto.Response.fromEntity(remittance);
    }

    public List<RemittanceDto> getRemittanceList(Long accountId, User user) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new CustomException(AccountErrorCode.ACCOUNT_NOT_FOUND));
        if (!Objects.equals(account.getUser().getId(), user.getId())) {
            throw new CustomException(AccountErrorCode.ACCOUNT_USER_UN_MATCH);
        }

        List<Remittance> remittanceList = remittanceRepository
                .findAllByAccountOrderByCreatedAtDesc(account);

        return remittanceList.stream().map(RemittanceDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RemittanceDto> getRemittanceList(
            Long accountId, User user, LocalDateTime startDate, LocalDateTime endDate
    ) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new CustomException(AccountErrorCode.ACCOUNT_NOT_FOUND));
        if (!Objects.equals(account.getUser().getId(), user.getId())) {
            throw new CustomException(AccountErrorCode.ACCOUNT_USER_UN_MATCH);
        }

        List<Remittance> remittanceList = remittanceRepository
                .findAllByAccountOrderByCreatedAtBetweenDesc(account, startDate, endDate);

        return remittanceList.stream().map(RemittanceDto::fromEntity)
                .collect(Collectors.toList());
    }
}
