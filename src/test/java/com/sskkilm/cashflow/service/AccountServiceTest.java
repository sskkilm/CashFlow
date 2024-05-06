package com.sskkilm.cashflow.service;

import com.sskkilm.cashflow.dto.AccountDto;
import com.sskkilm.cashflow.dto.CreateAccountDto;
import com.sskkilm.cashflow.dto.DeleteAccountDto;
import com.sskkilm.cashflow.dto.InactiveAccountDto;
import com.sskkilm.cashflow.entity.Account;
import com.sskkilm.cashflow.entity.User;
import com.sskkilm.cashflow.enums.AccountErrorCode;
import com.sskkilm.cashflow.enums.AccountStatus;
import com.sskkilm.cashflow.enums.Authority;
import com.sskkilm.cashflow.exception.CustomException;
import com.sskkilm.cashflow.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("계좌 생성 성공")
    void createAccount_success() {
        //given
        given(accountRepository.existsByAccountNumber(any()))
                .willReturn(false);
        given(accountRepository.findByUser(any()))
                .willReturn(List.of());
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountRepository.save(any()))
                .willReturn(
                        Account.builder()
                                .id(1L)
                                .accountNumber("1122334455")
                                .balance(100)
                                .status(AccountStatus.ACTIVE)
                                .user(user)
                                .build()
                );

        //when
        CreateAccountDto.Request request = new CreateAccountDto.Request("1234512345", 1000);
        CreateAccountDto.Response response = accountService.createAccount(request, user);

        //then
        assertEquals(1L, response.accountId());
        assertEquals("1122334455", response.accountNumber());
        assertEquals(100, response.balance());
        assertEquals(AccountStatus.ACTIVE, response.status());
        assertEquals(1L, response.userId());
    }

    @Test
    @DisplayName("계좌 생성 실패 - 이미 존재하는 계좌")
    void createAccount_fail_accountAlreadyExists() {
        //given
        given(accountRepository.existsByAccountNumber(any()))
                .willReturn(true);
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();

        //when
        CreateAccountDto.Request request = new CreateAccountDto.Request("1234512345", 1000);
        CustomException customException = assertThrows(CustomException.class, () -> accountService.createAccount(request, user));

        //then
        assertEquals(AccountErrorCode.ACCOUNT_ALREADY_EXISTS.getStatus(), customException.getErrorCode().getStatus());
        assertEquals(AccountErrorCode.ACCOUNT_ALREADY_EXISTS, customException.getErrorCode());
    }

    @Test
    @DisplayName("계좌 생성 실패 - 계좌는 최대 10개")
    void createAccount_fail_accountCreationLimit() {
        //given
        given(accountRepository.existsByAccountNumber(any()))
                .willReturn(false);
        List<Account> accountList = new ArrayList<>();
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        for (int i = 0; i < 10; i++) {
            accountList.add(Account.builder()
                    .user(user)
                    .build());
        }
        given(accountRepository.findByUser(any()))
                .willReturn(accountList);

        //when
        CreateAccountDto.Request request = new CreateAccountDto.Request("1234512345", 1000);
        CustomException customException = assertThrows(CustomException.class, () -> accountService.createAccount(request, user));

        //then
        assertEquals(AccountErrorCode.ACCOUNT_CREATION_LIMIT.getStatus(), customException.getErrorCode().getStatus());
        assertEquals(AccountErrorCode.ACCOUNT_CREATION_LIMIT, customException.getErrorCode());
    }

    @Test
    @DisplayName("계좌 해지 성공")
    void inactiveAccount_success() {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(
                        Account.builder()
                                .id(1L)
                                .status(AccountStatus.ACTIVE)
                                .user(user)
                                .build()));
        //when
        InactiveAccountDto.Response response = accountService.inactiveAccount(1L, user);

        //then
        assertEquals(1L, response.id());
        assertEquals(AccountStatus.INACTIVE, response.status());
        assertEquals(1L, response.userId());
    }

    @Test
    @DisplayName("계좌 해지 실패 - 존재하지 않는 계좌")
    void inactiveAccount_fail_accountNotFound() {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> accountService.inactiveAccount(1L, user)
        );

        //then
        assertEquals(AccountErrorCode.ACCOUNT_NOT_FOUND, customException.getErrorCode());
    }

    @Test
    @DisplayName("계좌 해지 실패 - 계좌 소유주 다름")
    void inactiveAccount_fail_accountUserUnMatch() {
        //given
        User user1 = User.builder()
                .id(1L)
                .loginId("root1")
                .password("root1")
                .role(Authority.ROLE_USER)
                .build();
        User user2 = User.builder()
                .id(2L)
                .loginId("root2")
                .password("root2")
                .role(Authority.ROLE_USER)
                .build();
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(
                        Account.builder()
                                .id(1L)
                                .status(AccountStatus.ACTIVE)
                                .user(user1)
                                .build())
                );

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> accountService.inactiveAccount(1L, user2)
        );

        //then
        assertEquals(AccountErrorCode.ACCOUNT_USER_UN_MATCH, customException.getErrorCode());
    }

    @Test
    @DisplayName("계좌 해지 실패 - 이미 해지된 계좌")
    void inactiveAccount_fail_accountAlreadyInactive() {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(
                        Account.builder()
                                .id(1L)
                                .status(AccountStatus.INACTIVE)
                                .user(user)
                                .build())
                );

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> accountService.inactiveAccount(1L, user)
        );

        //then
        assertEquals(AccountErrorCode.ACCOUNT_ALREADY_INACTIVE, customException.getErrorCode());
    }

    @Test
    @DisplayName("계좌 삭제 성공")
    void deleteAccount_success() {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(
                        Account.builder()
                                .id(1L)
                                .user(user)
                                .build())
                );

        //when
        DeleteAccountDto.Response response = accountService.deleteAccount(1L, user);

        //then
        assertEquals(1L, response.id());
        assertEquals(1L, response.userId());
    }

    @Test
    @DisplayName("계좌 삭제 실패 - 존재하지 않는 계좌")
    void deleteAccount_fail_AccountNotFound() {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> accountService.deleteAccount(1L, user)
        );

        //then
        assertEquals(AccountErrorCode.ACCOUNT_NOT_FOUND, customException.getErrorCode());
    }

    @Test
    @DisplayName("계좌 삭제 실패 - 계좌 소유주 다름")
    void deleteAccount_fail_AccountUserUnMatch() {
        //given
        User user1 = User.builder()
                .id(1L)
                .loginId("root1")
                .password("root1")
                .role(Authority.ROLE_USER)
                .build();
        User user2 = User.builder()
                .id(2L)
                .loginId("root2")
                .password("root2")
                .role(Authority.ROLE_USER)
                .build();
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(
                        Account.builder()
                                .id(1L)
                                .user(user1)
                                .build()
                ));

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> accountService.deleteAccount(1L, user2)
        );

        //then
        assertEquals(AccountErrorCode.ACCOUNT_USER_UN_MATCH, customException.getErrorCode());
    }

    @Test
    @DisplayName("전체 계좌 목록 조회")
    void getTotalAccountList() {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        List<Account> accountList = List.of(
                Account.builder()
                        .id(1L)
                        .user(user)
                        .createdAt(LocalDateTime.of(
                                LocalDate.of(2024, 5, 4),
                                LocalTime.of(6, 30)
                        ))
                        .build(),
                Account.builder()
                        .id(2L)
                        .user(user)
                        .createdAt(LocalDateTime.of(
                                LocalDate.of(2024, 5, 5),
                                LocalTime.of(6, 30)
                        ))
                        .build(),
                Account.builder()
                        .id(3L)
                        .user(user)
                        .createdAt(LocalDateTime.of(
                                LocalDate.of(2024, 5, 6),
                                LocalTime.of(6, 30)
                        ))
                        .build()
        );
        given(accountRepository.findAllByUserOrderByCreatedAt(user))
                .willReturn(accountList);

        //when
        List<AccountDto> totalAccountList = accountService.getTotalAccountList(user);

        //then
        assertEquals(1L, totalAccountList.get(0).accountId());
        assertEquals(2L, totalAccountList.get(1).accountId());
        assertEquals(3L, totalAccountList.get(2).accountId());
    }

    @Test
    @DisplayName("활성 계좌 목록 조회")
    void getActiveAccountList() {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        List<Account> accountList = List.of(
                Account.builder()
                        .id(1L)
                        .user(user)
                        .status(AccountStatus.ACTIVE)
                        .createdAt(LocalDateTime.of(
                                LocalDate.of(2024, 5, 4),
                                LocalTime.of(6, 30)
                        ))
                        .build(),
                Account.builder()
                        .id(2L)
                        .user(user)
                        .status(AccountStatus.ACTIVE)
                        .createdAt(LocalDateTime.of(
                                LocalDate.of(2024, 5, 5),
                                LocalTime.of(6, 30)
                        ))
                        .build(),
                Account.builder()
                        .id(3L)
                        .user(user)
                        .status(AccountStatus.ACTIVE)
                        .createdAt(LocalDateTime.of(
                                LocalDate.of(2024, 5, 6),
                                LocalTime.of(6, 30)
                        ))
                        .build()
        );
        given(accountRepository.findAllByUserAndStatusOrderByCreatedAt(user, AccountStatus.ACTIVE))
                .willReturn(accountList);

        //when
        List<AccountDto> activeAccountList = accountService.getActiveAccountList(user);

        //then
        assertEquals(1L, activeAccountList.get(0).accountId());
        assertEquals(AccountStatus.ACTIVE, activeAccountList.get(0).status());
        assertEquals(2L, activeAccountList.get(1).accountId());
        assertEquals(AccountStatus.ACTIVE, activeAccountList.get(0).status());
        assertEquals(3L, activeAccountList.get(2).accountId());
        assertEquals(AccountStatus.ACTIVE, activeAccountList.get(0).status());
    }

    @Test
    @DisplayName("비활성 계좌 목록 조회")
    void getInactiveAccountList() {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        List<Account> accountList = List.of(
                Account.builder()
                        .id(1L)
                        .user(user)
                        .status(AccountStatus.INACTIVE)
                        .createdAt(LocalDateTime.of(
                                LocalDate.of(2024, 5, 4),
                                LocalTime.of(6, 30)
                        ))
                        .build(),
                Account.builder()
                        .id(2L)
                        .user(user)
                        .status(AccountStatus.INACTIVE)
                        .createdAt(LocalDateTime.of(
                                LocalDate.of(2024, 5, 5),
                                LocalTime.of(6, 30)
                        ))
                        .build(),
                Account.builder()
                        .id(3L)
                        .user(user)
                        .status(AccountStatus.INACTIVE)
                        .createdAt(LocalDateTime.of(
                                LocalDate.of(2024, 5, 6),
                                LocalTime.of(6, 30)
                        ))
                        .build()
        );
        given(accountRepository.findAllByUserAndStatusOrderByCreatedAt(user, AccountStatus.INACTIVE))
                .willReturn(accountList);

        //when
        List<AccountDto> activeAccountList = accountService.getInactiveAccountList(user);

        //then
        assertEquals(1L, activeAccountList.get(0).accountId());
        assertEquals(AccountStatus.INACTIVE, activeAccountList.get(0).status());
        assertEquals(2L, activeAccountList.get(1).accountId());
        assertEquals(AccountStatus.INACTIVE, activeAccountList.get(1).status());
        assertEquals(3L, activeAccountList.get(2).accountId());
        assertEquals(AccountStatus.INACTIVE, activeAccountList.get(2).status());
    }
}