package com.example.auth.domain.auth.service;

import com.example.auth.domain.auth.dto.response.SignInResponseDto;
import com.example.auth.domain.auth.repository.RefreshTokenRepository;
import com.example.auth.domain.member.entity.Member;
import com.example.auth.domain.member.repository.MemberRepository;
import com.example.auth.global.exception.CustomException;
import com.example.auth.global.jwt.JwtUtil;
import com.example.auth.global.response.RefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    public SignInResponseDto refreshToken(String bearerToken) {
        String token = jwtUtil.substringToken(bearerToken);
        Claims claims;

        try {
            claims = jwtUtil.extractClaims(token);
        } catch (ExpiredJwtException e) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Refresh Token 만료. 다시 로그인 필요");
        }

        String tokenType = claims.get("tokenType", String.class);
        if (!"refresh".equalsIgnoreCase(tokenType)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Access Token이 제공되었습니다. Refresh Token 필요");
        }

        Long memberId = Long.parseLong(claims.getSubject());

        RefreshToken saved = refreshTokenRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(HttpStatus.UNAUTHORIZED, "Refresh Token 없음"));

        if (!saved.getToken().equals(token)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Refresh Token 불일치");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "유저 없음"));

        String newAccessToken = jwtUtil.createToken(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhoneNumber(),
                member.getAddress(),
                member.getUserRole(),
                member.getProvider()
        );
        String newRefreshToken = jwtUtil.createRefreshToken(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhoneNumber(),
                member.getAddress(),
                member.getUserRole(),
                member.getProvider()
        );
        saved.updateToken(newRefreshToken);

        return new SignInResponseDto(newAccessToken, newRefreshToken);
    }

}
