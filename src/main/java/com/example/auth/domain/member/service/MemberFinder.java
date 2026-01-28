package com.example.auth.domain.member.service;

import com.example.auth.domain.member.entity.Member;
import com.example.auth.domain.member.repository.MemberRepository;
import com.example.auth.global.exception.CustomException;
import com.example.auth.global.response.AuthMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberFinder {

    private final MemberRepository memberRepository;

    public void validateOwnership(AuthMember authMember, Long id) {
        if (!authMember.getId().equals(id)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다");
        }
    }

    public Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 회원정보입니다."));
    }
}
