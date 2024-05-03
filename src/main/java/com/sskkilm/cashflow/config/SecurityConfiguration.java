package com.sskkilm.cashflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity()
public class SecurityConfiguration {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {

        // csrf disable
        http
                .csrf((auth) -> auth.disable());
        // form 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());
        // http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());
        // 경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        // 인증, 인가없이 접근할 수 있는 경로
                        .requestMatchers("/", "/users/join", "/users/login").permitAll()
                        // 나머지 경로는 인증된 사용자만 접근 가능 (인증된 사용자 = USER 권한)
                        .anyRequest().authenticated());
        // 세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
