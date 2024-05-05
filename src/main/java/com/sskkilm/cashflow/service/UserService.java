package com.sskkilm.cashflow.service;

import com.sskkilm.cashflow.dto.JoinDto;
import com.sskkilm.cashflow.entity.User;
import com.sskkilm.cashflow.enums.Authority;
import com.sskkilm.cashflow.enums.UserErrorCode;
import com.sskkilm.cashflow.exception.CustomException;
import com.sskkilm.cashflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinDto.Response createUser(JoinDto.Request request) {
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new CustomException(UserErrorCode.ALREADY_EXIST_USER);
        }

        User user = userRepository.save(User.builder()
                .loginId(request.loginId())
                .password(bCryptPasswordEncoder.encode(request.password()))
                .role(Authority.ROLE_USER)
                .build());

        return JoinDto.Response.fromEntity(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLoginId(username)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        return user;
    }
}
