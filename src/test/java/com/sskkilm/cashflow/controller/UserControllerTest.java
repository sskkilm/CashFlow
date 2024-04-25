package com.sskkilm.cashflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sskkilm.cashflow.config.SecurityConfiguration;
import com.sskkilm.cashflow.dto.JoinDto;
import com.sskkilm.cashflow.exception.CustomException;
import com.sskkilm.cashflow.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.sskkilm.cashflow.enums.UserErrorCode.ALREADY_EXIST_USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfiguration.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공")
    void createUser_success() throws Exception {
        //given
        given(userService.createUser(any()))
                .willReturn(
                        JoinDto.Response.builder()
                                .id(1L)
                                .loginId("root")
                                .password("root")
                                .role("USER")
                                .build()
                );
        //when
        //then
        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new JoinDto.Request("root", "root")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.loginId").value("root"))
                .andExpect(jsonPath("$.password").value("root"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 사용자")
    void createUser_fail() throws Exception {
        //given
        given(userService.createUser(any()))
                .willThrow(new CustomException(ALREADY_EXIST_USER));
        //when
        //then
        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new JoinDto.Request("root", "root")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("ALREADY_EXIST_USER"))
                .andExpect(jsonPath("$.message").value(ALREADY_EXIST_USER.getMessage()))
                .andDo(print());
    }
}