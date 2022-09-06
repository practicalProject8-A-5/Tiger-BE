package com.tiger.repository;

import com.tiger.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmailAndIsValid(String email, Boolean isValid);

    Optional<Member> findByIdAndIsValid(Long id, Boolean isValid);
}
