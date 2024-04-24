package com.sskkilm.cashflow.service;

import com.sskkilm.cashflow.dto.JoinDto;
import com.sskkilm.cashflow.entity.User;
import com.sskkilm.cashflow.enums.UserErrorCode;
import com.sskkilm.cashflow.exception.CustomException;
import com.sskkilm.cashflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinDto.Response createUser(JoinDto.Request request) {
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new CustomException(UserErrorCode.ALREADY_EXIST_USER);
        }

        User user = userRepository.save(User.builder()
                .loginId(request.loginId())
                .password(bCryptPasswordEncoder.encode(request.password()))
                .role("USER")
                .build());

        return JoinDto.Response.fromEntity(user);
    }
}
