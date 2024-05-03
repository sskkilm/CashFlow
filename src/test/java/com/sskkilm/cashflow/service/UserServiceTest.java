package com.sskkilm.cashflow.service;

import com.sskkilm.cashflow.dto.JoinDto;
import com.sskkilm.cashflow.entity.User;
import com.sskkilm.cashflow.enums.Authority;
import com.sskkilm.cashflow.enums.UserErrorCode;
import com.sskkilm.cashflow.exception.CustomException;
import com.sskkilm.cashflow.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원가입 성공")
    void createUser_success() {
        //given
        given(userRepository.existsByLoginId(any()))
                .willReturn(false);
        given(userRepository.save(any()))
                .willReturn(User.builder()
                        .loginId("root")
                        .password("root")
                        .role(Authority.ROLE_USER)
                        .build());
        given(bCryptPasswordEncoder.encode(any()))
                .willReturn("root");

        //when
        JoinDto.Response response = userService.createUser(
                new JoinDto.Request("root", "root")
        );

        //then
        assertEquals("root", response.loginId());
        assertEquals("root", response.password());
        assertEquals(Authority.ROLE_USER, response.role());
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 사용자")
    void createUser_fail() {
        //given
        given(userRepository.existsByLoginId(any()))
                .willReturn(true);

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> userService.createUser(
                        new JoinDto.Request("root", "root")
                ));

        //then
        assertEquals(400, customException.getErrorCode().getStatus());
        assertEquals(UserErrorCode.ALREADY_EXIST_USER, customException.getErrorCode());
    }

}