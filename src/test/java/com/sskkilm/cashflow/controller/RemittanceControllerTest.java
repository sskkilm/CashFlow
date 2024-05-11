package com.sskkilm.cashflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sskkilm.cashflow.config.SecurityConfiguration;
import com.sskkilm.cashflow.dto.CreateRemittanceDto;
import com.sskkilm.cashflow.entity.User;
import com.sskkilm.cashflow.enums.Authority;
import com.sskkilm.cashflow.enums.GlobalErrorCode;
import com.sskkilm.cashflow.service.RemittanceService;
import com.sskkilm.cashflow.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RemittanceController.class)
@Import(SecurityConfiguration.class)
class RemittanceControllerTest {

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private RemittanceService remittanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("송금 성공")
    void createRemittance_success() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(remittanceService.createRemittance(any(), any()))
                .willReturn(
                        new CreateRemittanceDto.Response(
                                "1122334455",
                                1000,
                                0,
                                LocalDateTime.of(
                                        2024, 5, 5,
                                        6, 30
                                )
                        )
                );

        //when
        //then
        mockMvc.perform(post("/remittances")
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateRemittanceDto.Request(
                                        1L,
                                        "1122334455",
                                        1000
                                )
                        ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.receivingAccountNumber")
                        .value("1122334455"))
                .andExpect(jsonPath("$.remittanceAmount")
                        .value(1000))
                .andExpect(jsonPath("$.accountBalanceSnapshot")
                        .value(0))
                .andExpect(jsonPath("$.createdAt")
                        .value("2024-05-05T06:30:00"))
                .andDo(print());
    }

    @Test
    @DisplayName("송금 실패 - 계좌 아이디 1보다 작음")
    void createRemittance_fail_accountIdLessThan1() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        //when
        //then
        mockMvc.perform(post("/remittances")
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateRemittanceDto.Request(
                                        0L,
                                        "1122334455",
                                        1000
                                )
                        ))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value(GlobalErrorCode.INVALID_REQUEST.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("송금 실패 - 수금 계좌번호 공백")
    void createRemittance_fail_receivingAccountNumberBlank() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        //when
        //then
        mockMvc.perform(post("/remittances")
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateRemittanceDto.Request(
                                        1L,
                                        "",
                                        1000
                                )
                        ))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value(GlobalErrorCode.INVALID_REQUEST.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("송금 실패 - 송금액 0보다 작음")
    void createRemittance_fail_remittanceAmountLessThan0() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        //when
        //then
        mockMvc.perform(post("/remittances")
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateRemittanceDto.Request(
                                        1L,
                                        "1122334455",
                                        -1
                                )
                        ))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value(GlobalErrorCode.INVALID_REQUEST.getMessage()))
                .andDo(print());
    }
}