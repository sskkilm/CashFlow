package com.sskkilm.cashflow.dto;

import com.sskkilm.cashflow.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public class JoinDto {
    public record Request(
            @NotBlank(message = "아이디는 공백일 수 없습니다.")
            String loginId,
            @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
            String password
    ) {

    }

    @Builder
    public record Response(
            Long id,
            String loginId,
            String password,
            String role
    ) {
        public static Response fromEntity(User user) {
            return Response.builder()
                    .id(user.getId())
                    .loginId(user.getLoginId())
                    .password(user.getPassword())
                    .role(user.getRole())
                    .build();
        }
    }
}
