package com.example.auth.domain.member.service;

import com.example.auth.domain.member.dto.request.MemberDeleteRequestDto;
import com.example.auth.domain.member.dto.request.MemberUpdateRequestDto;
import com.example.auth.domain.member.dto.request.PasswordUpdateRequestDto;
import com.example.auth.domain.member.dto.response.MemberResponseDto;
import com.example.auth.domain.member.entity.Member;
import com.example.auth.domain.member.repository.MemberRepository;
import com.example.auth.global.exception.CustomException;
import com.example.auth.global.response.AuthMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberFinder memberFinder;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberResponseDto findMember(AuthMember authMember) {
        Member member = memberFinder.findMemberById(authMember.getId());

        return MemberResponseDto.from(member);
    }

    @Transactional
    public void updateMember(
            AuthMember authMember, MemberUpdateRequestDto requestDto
    ) {
        Member member = memberFinder.findMemberById(authMember.getId());

        member.updateMemberInfo(requestDto.getName(), requestDto.getPhoneNumber(), requestDto.getAddress());
    }

    @Transactional
    public void updatePassword(
            AuthMember authMember, PasswordUpdateRequestDto requestDto
    ) {
        Member member = memberFinder.findMemberById(authMember.getId());

        if(!passwordEncoder.matches(requestDto.getOldPassword(), member.getPassword())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "기존 비밀번호가 일치하지 않습니다.");
        }

        if (passwordEncoder.matches(requestDto.getNewPassword(), member.getPassword())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "동일한 비밀번호로 변경할 수 없습니다.");
        }

        String newEncodedPassword = passwordEncoder.encode(requestDto.getNewPassword());

        member.updatePassword(newEncodedPassword);
    }

    @Transactional
    public Long deleteMember(
            AuthMember authMember, MemberDeleteRequestDto requestDto
    ) {
        Member member = memberFinder.findMemberById(authMember.getId());

        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "잘못된 비밀번호입니다.");
        }

        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "비밀번호 확인에 실패했습니다.");
        }

        memberRepository.delete(member);

        return member.getId();
    }

}