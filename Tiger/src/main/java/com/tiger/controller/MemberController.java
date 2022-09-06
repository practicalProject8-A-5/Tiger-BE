package com.tiger.controller;


import com.tiger.domain.CommonResponseDto;
import com.tiger.domain.TokenDto;
import com.tiger.domain.member.Member;
import com.tiger.domain.member.dto.LoginRequestDto;
import com.tiger.domain.member.dto.RegisterRequestDto;
import com.tiger.exception.StatusCode;
import com.tiger.service.MemberService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RequiredArgsConstructor
@RestController @RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;


    @PostMapping("/register")
    public CommonResponseDto<?> register(@RequestBody RegisterRequestDto registerRequestDto) {

        String name = memberService.register(registerRequestDto);

        return CommonResponseDto.success(StatusCode.USER_CREATED, name);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {

        TokenDto token = memberService.login(loginRequestDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token.getAuthorization());
        headers.add("RefreshToken", token.getRefreshToken());

        Member member = memberService.findMemberByEmail(loginRequestDto.getEmail());

        return ResponseEntity.ok().headers(headers)
                .body(CommonResponseDto.success(StatusCode.LOGIN_SUCCESS,
                        Map.of("email", member.getEmail(),
                                "name", member.getName())));
    }

    @DeleteMapping("/logout")
    public CommonResponseDto<?> logout(HttpServletRequest httpServletRequest) {

        String name = memberService.logout(httpServletRequest);

        return CommonResponseDto.success(StatusCode.LOGOUT_SUCCESS, name);
    }

}
