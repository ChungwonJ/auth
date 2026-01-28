package com.example.auth.domain.auth.service;

import com.example.auth.domain.auth.dto.request.SignInRequestDto;
import com.example.auth.domain.auth.dto.request.SignUpRequestDto;
import com.example.auth.domain.auth.dto.response.SignInResponseDto;
import com.example.auth.domain.auth.dto.response.SignUpResponseDto;
import com.example.auth.domain.auth.repository.RefreshTokenRepository;
import com.example.auth.domain.member.entity.Member;
import com.example.auth.domain.member.repository.MemberRepository;
import com.example.auth.global.annotation.UserRole;
import com.example.auth.global.exception.CustomException;
import com.example.auth.global.jwt.JwtUtil;
import com.example.auth.global.response.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public SignUpResponseDto signUp(SignUpRequestDto requestDto) {
        String normalizedEmail = requestDto.getEmail().toLowerCase();

        if (memberRepository.existsByEmail(normalizedEmail)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다.");
        }

        Member newMember = Member.builder()
                .email(normalizedEmail)
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .userRole(UserRole.ROLE_USER)
                .build();

        Member savedMember = memberRepository.save(newMember);

        String accessToken = jwtUtil.createToken(
                savedMember.getId(),
                savedMember.getEmail(),
                savedMember.getName(),
                savedMember.getPhoneNumber(),
                savedMember.getAddress(),
                savedMember.getUserRole(),
                savedMember.getProvider()
        );
        String refreshToken = jwtUtil.createRefreshToken(
                savedMember.getId(),
                savedMember.getEmail(),
                savedMember.getName(),
                savedMember.getPhoneNumber(),
                savedMember.getAddress(),
                savedMember.getUserRole(),
                savedMember.getProvider()
        );

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .memberId(savedMember.getId())
                        .token(refreshToken)
                        .build()
        );

        return SignUpResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(savedMember.getId())
                .email(savedMember.getEmail())
                .name(savedMember.getName())
                .phoneNumber(savedMember.getPhoneNumber())
                .address(savedMember.getAddress())
                .userRole(savedMember.getUserRole().name())
                .build();
    }

    @Transactional
    public SignInResponseDto signIn(SignInRequestDto requestDto) {
        String normalizedEmail = requestDto.getEmail().toLowerCase();

        Member member = memberRepository.findByEmail(normalizedEmail).orElseThrow(
                () -> new CustomException(HttpStatus.BAD_REQUEST, "가입되지 않은 유저입니다.")
        );

        if (member.getIsDeleted()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "탈퇴한 사용자입니다.");
        }

        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "잘못된 비밀번호입니다.");
        }

        String accessToken = jwtUtil.createToken(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhoneNumber(),
                member.getAddress(),
                member.getUserRole(),
                member.getProvider()
        );

        String refreshToken = jwtUtil.createRefreshToken(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhoneNumber(),
                member.getAddress(),
                member.getUserRole(),
                member.getProvider()
        );

        refreshTokenRepository.findById(member.getId()).ifPresentOrElse(existing -> existing.updateToken(refreshToken),
                () -> refreshTokenRepository.save(
                        RefreshToken.builder()
                                .memberId(member.getId())
                                .token(refreshToken)
                                .build()
                )
        );

        return new SignInResponseDto(accessToken, refreshToken);
    }

}
