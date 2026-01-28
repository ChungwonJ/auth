package com.example.auth.domain.member.dto.response;

import com.example.auth.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class MemberResponseDto {

    private final Long id;
    private final String email;
    private final String name;
    private final String phoneNumber;
    private final String address;
    private final String userRole;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Boolean isDeleted;

    public static MemberResponseDto from(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .userRole(member.getUserRole().name())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .isDeleted(member.getIsDeleted())
                .build();
    }

}