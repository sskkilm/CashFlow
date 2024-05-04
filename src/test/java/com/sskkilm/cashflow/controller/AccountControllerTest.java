package com.sskkilm.cashflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sskkilm.cashflow.config.SecurityConfiguration;
import com.sskkilm.cashflow.dto.CreateAccountDto;
import com.sskkilm.cashflow.entity.User;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}