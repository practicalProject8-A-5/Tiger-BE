package com.tiger.service;

import com.tiger.domain.UserDetailsImpl;
import com.tiger.domain.member.Member;
import com.tiger.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Member> member = memberRepository.findByEmail(email);
        return member
                .map(UserDetailsImpl::new)
                .orElseThrow(()-> new UsernameNotFoundException("아이디를 찾을 수 없습니다."));
    }
}
