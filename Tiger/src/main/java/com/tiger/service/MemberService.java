package com.tiger.service;

import com.tiger.config.security.jwt.TokenProvider;
import com.tiger.domain.TokenDto;
import com.tiger.domain.UserDetailsImpl;
import com.tiger.domain.member.Member;
import com.tiger.domain.member.dto.LoginRequestDto;
import com.tiger.domain.member.dto.RegisterRequestDto;
import com.tiger.exception.CustomException;
import com.tiger.exception.StatusCode;
import com.tiger.repository.MemberRepository;
import com.tiger.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    private final TokenProvider tokenProvider;

    private final RefreshTokenRepository refreshTokenRepository;


    @Transactional
    public String register(RegisterRequestDto registerRequestDto) {

        String password = passwordEncoder.encode(registerRequestDto.getPassword());

        String a = "-" + ((int)((Math.random() * (9999 - 1000 + 1)) + 1000));
        String b = "-" + ((int)((Math.random() * (9999 - 1000 + 1)) + 1000));
        String tel = "050" + a + b;

        Member member = Member.builder()
                .email(registerRequestDto.getEmail())
                .name(registerRequestDto.getName())
                .password(password)
                .tel(tel)
                .isValid(true)
                .build();

        return memberRepository.save(member).getName();
    }


    public TokenDto login(LoginRequestDto loginRequestDto) {

        String email = loginRequestDto.getEmail();

        Member member = findMemberByEmail(email);

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new CustomException(StatusCode.INVALID_PASSWORD);
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "");

        return tokenProvider.generateTokenDto(authentication);
    }

    @Transactional
    public String logout(HttpServletRequest httpServletRequest) {
        if (!tokenProvider.validateToken(httpServletRequest.getHeader("RefreshToken"))) {
            throw new CustomException(StatusCode.INVALID_AUTH_TOKEN);
        }
        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            throw new CustomException(StatusCode.USER_NOT_FOUND);
        }

        refreshTokenRepository.deleteByMember(member);
        return member.getName();
    }

    public Member findMemberByEmail(String email) {

        return memberRepository.findByEmailAndIsValid(email, true).orElseThrow(()-> {
            throw new CustomException(StatusCode.INVALID_EMAIL);
        });
    }
}

