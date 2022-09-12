package com.tiger.utils;

import com.tiger.domain.member.Member;

import com.tiger.exception.CustomException;
import com.tiger.repository.MemberRepository;
import com.tiger.repository.OpenDateRepository;
import com.tiger.repository.OrderRepository;
import com.tiger.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.tiger.exception.StatusCode.*;

@Component
@RequiredArgsConstructor
public class CheckUtil {


    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final OpenDateRepository openDateRepository;


    // 회원 검증
    @Transactional(readOnly = true)
    public Member validateMember(long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
    }
}