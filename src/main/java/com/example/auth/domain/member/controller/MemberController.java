package com.example.auth.domain.member.controller;

import com.example.auth.domain.member.dto.request.MemberDeleteRequestDto;
import com.example.auth.domain.member.dto.request.MemberUpdateRequestDto;
import com.example.auth.domain.member.dto.request.PasswordUpdateRequestDto;
import com.example.auth.domain.member.dto.response.MemberResponseDto;
import com.example.auth.domain.member.service.MemberService;
import com.example.auth.global.annotation.Member;
import com.example.auth.global.response.ApiResponse;
import com.example.auth.global.response.AuthMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Member
    @GetMapping
    public ResponseEntity<ApiResponse<MemberResponseDto>> findMember(
            @AuthenticationPrincipal AuthMember authMember
    ) {
        return ResponseEntity.ok(ApiResponse.of(
                memberService.findMember(authMember)
        ));
    }

    @Member
    @PutMapping
    public ResponseEntity<ApiResponse<String>> updateMember(
            @AuthenticationPrincipal AuthMember authMember,
            @Valid @RequestBody MemberUpdateRequestDto requestDto
    ) {
        memberService.updateMember(authMember, requestDto);
        return ResponseEntity.ok(ApiResponse.of("회원 정보 수정이 완료되었습니다."));
    }

    @Member
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<String>> updatePassword(
            @AuthenticationPrincipal AuthMember authMember,
            @Valid @RequestBody PasswordUpdateRequestDto requestDto
    ) {
        memberService.updatePassword(authMember, requestDto);
        return ResponseEntity.ok(ApiResponse.of("비밀번호 수정이 완료되었습니다."));
    }

    @Member
    @DeleteMapping
    public ResponseEntity<ApiResponse<Long>> deleteMember(
            @AuthenticationPrincipal AuthMember authMember,
            @Valid @RequestBody MemberDeleteRequestDto requestDto
    ) {
        return ResponseEntity.ok(ApiResponse.of(
                memberService.deleteMember(authMember, requestDto)
        ));
    }

}