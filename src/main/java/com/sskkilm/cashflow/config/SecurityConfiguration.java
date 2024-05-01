package com.sskkilm.cashflow.config;

import com.sskkilm.cashflow.filter.JwtAuthenticationFilter;
import com.sskkilm.cashflow.filter.LoginFilter;
import com.sskkilm.cashflow.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CsrfFilter 비활성
        http
                .csrf((auth) -> auth.disable());
        // UsernamePasswordAuthenticationFilter 비활성
        http
                .formLogin((auth) -> auth.disable());
        // BasicAuthenticationFilter 비활성
        http
                .httpBasic((auth) -> auth.disable());
        // 경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        // 인증, 인가없이 접근할 수 있는 경로
                        .requestMatchers("/", "/users/join", "/users/login").permitAll()
                        // 나머지 경로는 인증된 사용자만 접근 가능 (인증된 사용자 = USER 권한)
                        .anyRequest().authenticated());
        // 로그인 필터 전에 jwt 검증 필터 등록
        http
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), LoginFilter.class);
        // 로그인이 성공하면 jwt를 발급해야하므로 UsernamePasswordAuthenticationFilter 자리에
        // 커스텀 필터 등록
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil)
                        , UsernamePasswordAuthenticationFilter.class);
        // 세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
