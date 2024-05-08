package com.sskkilm.cashflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sskkilm.cashflow.config.SecurityConfiguration;
import com.sskkilm.cashflow.dto.*;
import com.sskkilm.cashflow.entity.User;
import com.sskkilm.cashflow.enums.AccountStatus;
import com.sskkilm.cashflow.enums.Authority;
import com.sskkilm.cashflow.enums.GlobalErrorCode;
import com.sskkilm.cashflow.service.AccountService;
import com.sskkilm.cashflow.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@Import(SecurityConfiguration.class)
class AccountControllerTest {

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("계좌 생성")
    void createAccount() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountService.createAccount(any(), any()))
                .willReturn(
                        CreateAccountDto.Response.builder()
                                .accountNumber("1122334455")
                                .balance(1000)
                                .userId(user.getId())
                                .build()
                );

        //when
        //then
        mockMvc.perform(post("/accounts")
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateAccountDto.Request("1122334455", 1000)
                        ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("1122334455"))
                .andExpect(jsonPath("$.balance").value(1000))
                .andExpect(jsonPath("$.userId").value(1L))
                .andDo(print());
    }

    @Test
    @DisplayName("계좌 생성 실패 - 계좌번호 공백")
    void createAccount_fail_accountNumberBlank() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        //when
        //then
        mockMvc.perform(post("/accounts")
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateAccountDto.Request("", 1000)
                        ))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value(GlobalErrorCode.INVALID_REQUEST.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("계좌 생성 실패 - 초기 잔액이 0보다 작음")
    void createAccount_fail_initialBalance() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        //when
        //then
        mockMvc.perform(post("/accounts")
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateAccountDto.Request("1122334455", -1)
                        ))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value(GlobalErrorCode.INVALID_REQUEST.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("계좌 해지")
    void inactiveAccount() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountService.inactiveAccount(1L, user))
                .willReturn(InactiveAccountDto.Response.builder()
                        .id(1L)
                        .accountNumber("11223344")
                        .balance(1000)
                        .status(AccountStatus.INACTIVE)
                        .userId(1L)
                        .build());
        //when
        //then
        mockMvc.perform(patch("/accounts/1")
                        .with(user(user))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.accountNumber").value("11223344"))
                .andExpect(jsonPath("$.balance").value(1000))
                .andExpect(jsonPath("$.status").value("INACTIVE"))
                .andExpect(jsonPath("$.userId").value(1L))
                .andDo(print());

    }

    @Test
    @DisplayName("계좌 삭제")
    void deleteAccount() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountService.deleteAccount(1L, user))
                .willReturn(DeleteAccountDto.Response.builder()
                        .id(1L)
                        .accountNumber("11223344")
                        .balance(1000)
                        .userId(1L)
                        .build());

        //when
        //then
        mockMvc.perform(delete("/accounts/1")
                        .with(user(user))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.accountNumber").value("11223344"))
                .andExpect(jsonPath("$.balance").value(1000))
                .andExpect(jsonPath("$.userId").value(1L))
                .andDo(print());
    }

    @Test
    @DisplayName("전체 계좌 목록 조회")
    void getTotalAccountList() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountService.getTotalAccountList(user))
                .willReturn(
                        List.of(
                                AccountDto.builder()
                                        .accountId(1L)
                                        .build(),
                                AccountDto.builder()
                                        .accountId(2L)
                                        .build(),
                                AccountDto.builder()
                                        .accountId(3L)
                                        .build()
                        )
                );

        //when
        //then
        mockMvc.perform(get("/accounts")
                        .with(user(user))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value(1L))
                .andExpect(jsonPath("$[1].accountId").value(2L))
                .andExpect(jsonPath("$[2].accountId").value(3L))
                .andDo(print());
    }

    @Test
    @DisplayName("활성 계좌 목록 조회")
    void getActiveAccountList() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountService.getActiveAccountList(user))
                .willReturn(
                        List.of(
                                AccountDto.builder()
                                        .accountId(1L)
                                        .status(AccountStatus.ACTIVE)
                                        .build(),
                                AccountDto.builder()
                                        .accountId(2L)
                                        .status(AccountStatus.ACTIVE)
                                        .build(),
                                AccountDto.builder()
                                        .accountId(3L)
                                        .status(AccountStatus.ACTIVE)
                                        .build()
                        )
                );

        //when
        //then
        mockMvc.perform(get("/accounts/active")
                        .with(user(user))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value(1L))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[1].accountId").value(2L))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[2].accountId").value(3L))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andDo(print());
    }

    @Test
    @DisplayName("비활성 계좌 목록 조회")
    void getInactiveAccountList() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountService.getInactiveAccountList(user))
                .willReturn(
                        List.of(
                                AccountDto.builder()
                                        .accountId(1L)
                                        .status(AccountStatus.INACTIVE)
                                        .build(),
                                AccountDto.builder()
                                        .accountId(2L)
                                        .status(AccountStatus.INACTIVE)
                                        .build(),
                                AccountDto.builder()
                                        .accountId(3L)
                                        .status(AccountStatus.INACTIVE)
                                        .build()
                        )
                );

        //when
        //then
        mockMvc.perform(get("/accounts/inactive")
                        .with(user(user))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value(1L))
                .andExpect(jsonPath("$[0].status").value("INACTIVE"))
                .andExpect(jsonPath("$[1].accountId").value(2L))
                .andExpect(jsonPath("$[0].status").value("INACTIVE"))
                .andExpect(jsonPath("$[2].accountId").value(3L))
                .andExpect(jsonPath("$[0].status").value("INACTIVE"))
                .andDo(print());
    }

    @Test
    @DisplayName("특정 계좌 조회")
    void getAccount() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        LocalDateTime createdAt = LocalDateTime.of(
                LocalDate.of(2024, 5, 5),
                LocalTime.of(6, 30)
        );
        LocalDateTime modifiedAt = LocalDateTime.of(
                LocalDate.of(2024, 5, 5),
                LocalTime.of(7, 30)
        );
        given(accountService.getAccount(1L, user))
                .willReturn(
                        GetAccountDto.builder()
                                .accountId(1L)
                                .accountNumber("1122334455")
                                .balance(1000)
                                .status(AccountStatus.ACTIVE)
                                .createdAt(createdAt)
                                .modifiedAt(modifiedAt)
                                .build()
                );
        //when
        //then
        mockMvc.perform(get("/accounts/1")
                        .with(user(user))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1L))
                .andExpect(jsonPath("$.accountNumber").value("1122334455"))
                .andExpect(jsonPath("$.balance").value(1000))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.createdAt").value("2024-05-05T06:30:00"))
                .andExpect(jsonPath("$.modifiedAt").value("2024-05-05T07:30:00"))
                .andDo(print());
    }

    @Test
    @DisplayName("입금 성공")
    void deposit_success() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountService.deposit(any(), any()))
                .willReturn(
                        DepositDto.Response.builder()
                                .accountId(1L)
                                .balanceBeforeDeposit(1000)
                                .depositAmount(1000)
                                .balanceAfterDeposit(2000)
                                .build()
                );

        //when
        //then
        mockMvc.perform(patch("/accounts/deposit")
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DepositDto.Request(1L, 1000)
                        ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1L))
                .andExpect(jsonPath("$.balanceBeforeDeposit").value(1000))
                .andExpect(jsonPath("$.depositAmount").value(1000))
                .andExpect(jsonPath("$.balanceAfterDeposit").value(2000))
                .andDo(print());
    }

    @Test
    @DisplayName("입금 실패 - 계좌 아이디 1보다 작음")
    void deposit_fail_accountIdLessThan1() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        //when
        //then
        mockMvc.perform(patch("/accounts/deposit")
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DepositDto.Request(0L, 1000)
                        ))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value(GlobalErrorCode.INVALID_REQUEST.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("입금 실패 - 입금액 0보다 작음")
    void deposit_fail_depositAmountLessThan0() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        //when
        //then
        mockMvc.perform(patch("/accounts/deposit")
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DepositDto.Request(1L, -1)
                        ))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value(GlobalErrorCode.INVALID_REQUEST.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("출금 성공")
    void withdraw_success() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountService.withdraw(any(), any()))
                .willReturn(
                        WithdrawDto.Response.builder()
                                .accountId(1L)
                                .balanceBeforeWithdraw(2000)
                                .withdrawAmount(1000)
                                .balanceAfterWithdraw(1000)
                                .build()
                );
        //when
        //then
        mockMvc.perform(patch("/accounts/withdraw")
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new WithdrawDto.Request(1L, 1000)
                        ))
                )
                .andExpect(jsonPath("$.accountId").value(1L))
                .andExpect(jsonPath("$.balanceBeforeWithdraw").value(2000))
                .andExpect(jsonPath("$.withdrawAmount").value(1000))
                .andExpect(jsonPath("$.balanceAfterWithdraw").value(1000))
                .andDo(print());
    }

    @Test
    @DisplayName("출금 실패 - 계좌 아이디 1보다 작음")
    void withdraw_fail_accountIdLessThan1() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        //when
        //then
        mockMvc.perform(patch("/accounts/withdraw")
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new WithdrawDto.Request(0L, 1000)
                        ))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value(GlobalErrorCode.INVALID_REQUEST.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("출금 실패 - 출금액 0보다 작음")
    void withdraw_fail_depositAmountLessThan0() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        //when
        //then
        mockMvc.perform(patch("/accounts/withdraw")
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new WithdrawDto.Request(1L, -1)
                        ))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value(GlobalErrorCode.INVALID_REQUEST.getMessage()))
                .andDo(print());
    }
}