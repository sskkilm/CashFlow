package com.sskkilm.cashflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sskkilm.cashflow.config.AppConfiguration;
import com.sskkilm.cashflow.config.SecurityConfiguration;
import com.sskkilm.cashflow.dto.JoinDto;
import com.sskkilm.cashflow.entity.User;
import com.sskkilm.cashflow.enums.Authority;
import com.sskkilm.cashflow.exception.CustomException;
import com.sskkilm.cashflow.service.UserService;
import com.sskkilm.cashflow.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static com.sskkilm.cashflow.enums.UserErrorCode.ALREADY_EXIST_USER;
import static com.sskkilm.cashflow.enums.UserErrorCode.LOGIN_FAILED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({SecurityConfiguration.class, AppConfiguration.class})
class UserControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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
                                .role(Authority.ROLE_USER)
                                .build()
                );
        //when
        //then
        mockMvc.perform(post("/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new JoinDto.Request("root", "root")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.loginId").value("root"))
                .andExpect(jsonPath("$.password").value("root"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"))
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
        mockMvc.perform(post("/users/join")
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

    @Test
    @DisplayName("로그인 성공")
    void login_success() throws Exception {
        //given
        given(userService.loadUserByUsername(any()))
                .willReturn(User.builder()
                        .loginId("root")
                        .password(bCryptPasswordEncoder.encode("root"))
                        .role(Authority.ROLE_USER)
                        .build()
                );
        given(jwtUtil.createJwt(any(), any(), anyLong()))
                .willReturn("TEST_TOKEN");

        //when
        //then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("loginId", "root")
                        .param("password", "root"))
                .andExpect(header().string("Authorization", "Bearer TEST_TOKEN"))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_fail() throws Exception {
        //given
        given(userService.loadUserByUsername(any()))
                .willThrow(new UsernameNotFoundException("user not found"));

        //when
        //then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("loginId", "root")
                        .param("password", "root"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.errorCode").value("LOGIN_FAILED"))
                .andExpect(jsonPath("$.message").value(LOGIN_FAILED.getMessage()))
                .andDo(print());
    }
}