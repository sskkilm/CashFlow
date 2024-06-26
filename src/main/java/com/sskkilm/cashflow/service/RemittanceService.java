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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

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

    public Slice<RemittanceDto> getRemittanceList(Pageable pageable, Long accountId, User user) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new CustomException(AccountErrorCode.ACCOUNT_NOT_FOUND));
        if (!Objects.equals(account.getUser().getId(), user.getId())) {
            throw new CustomException(AccountErrorCode.ACCOUNT_USER_UN_MATCH);
        }

        Slice<Remittance> remittancePage = remittanceRepository
                .findAllByAccount(account, pageable);

        return remittancePage.map(RemittanceDto::fromEntity);
    }

    public Page<RemittanceDto> getRemittanceList(
            Pageable pageable, Long accountId, User user, LocalDateTime startDate, LocalDateTime endDate
    ) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new CustomException(AccountErrorCode.ACCOUNT_NOT_FOUND));
        if (!Objects.equals(account.getUser().getId(), user.getId())) {
            throw new CustomException(AccountErrorCode.ACCOUNT_USER_UN_MATCH);
        }

        // 송금 이력 조회를 시작일부터 최대 1년까지만 가능하도록 설정
        if (startDate.plusYears(1).isBefore(endDate)) {
            throw new CustomException(RemittanceErrorCode.REMITTANCE_HISTORY_INQUIRY_PERIOD_LIMITED);
        }

        Page<Remittance> remittanceList = remittanceRepository
                .findAllByAccountAndCreatedAt(account, startDate, endDate, pageable);

        return remittanceList.map(RemittanceDto::fromEntity);
    }
}
