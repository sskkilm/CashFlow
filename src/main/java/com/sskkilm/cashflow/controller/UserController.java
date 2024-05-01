package com.sskkilm.cashflow.controller;

import com.sskkilm.cashflow.dto.JoinDto;
import com.sskkilm.cashflow.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users/join")
    public JoinDto.Response createUser(
            @RequestBody @Valid JoinDto.Request request
    ) {
        return userService.createUser(request);
    }

    // 로그인 처리는 필터단에서 일어나도록 구현
    // 따로 컨트롤러 로직 작성할 필요없음
    // @PostMapping("/users/login")

}
