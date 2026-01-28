package com.example.auth.domain.member.controller;

import com.example.auth.domain.member.dto.response.MemberResponseDto;
import com.example.auth.domain.member.service.AdminService;
import com.example.auth.global.annotation.Admin;
import com.example.auth.global.response.ApiResponse;
import com.example.auth.global.response.PageCond;
import com.example.auth.global.response.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/members")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Admin
    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<MemberResponseDto>> findMember(
            @PathVariable Long memberId
    ) {
        return ResponseEntity.ok(ApiResponse.of(
                adminService.findMember(memberId)
        ));
    }

    @Admin
    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponseDto>>> findAllMembers(
            @ModelAttribute PageCond pagecond
    ) {
        Page<MemberResponseDto> responses = adminService.findAllMembers(pagecond);
        PageInfo pageInfo = PageInfo.builder()
                .pageNum(pagecond.getPageNum())
                .pageSize(pagecond.getPageSize())
                .totalElement(responses.getTotalElements())
                .totalPage(responses.getTotalPages())
                .build();

        return ResponseEntity.ok(ApiResponse.of(responses.getContent(), pageInfo));
    }

    @Admin
    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Long>> deleteMember(
            @PathVariable Long memberId
    ) {
        return ResponseEntity.ok(ApiResponse.of(
                adminService.deleteMember(memberId)
        ));
    }
}