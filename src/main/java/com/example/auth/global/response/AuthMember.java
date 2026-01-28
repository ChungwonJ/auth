package com.example.auth.global.response;

import com.example.auth.global.annotation.UserRole;
import com.example.auth.global.exception.CustomException;
import io.jsonwebtoken.Claims;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class AuthMember {

    private final Long id;
    private final String email;
    private final String name;
    private final String phoneNumber;
    private final String address;
    private final Set<GrantedAuthority> authorities;
    private final String provider;

    // 단일 권한 생성자 (JwtUtil이 현재 단일 Role을 주입 중)
    public AuthMember(Long id, String email, String name, String phoneNumber, String address, UserRole userRole, String provider) {
        this(id, email, name, phoneNumber, address, Set.of(userRole), provider);
    }

    // 다중 권한 생성자
    public AuthMember(Long id, String email, String name, String phoneNumber, String address, Set<UserRole> userRoles, String provider) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.authorities = userRoles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
        this.provider = provider;
    }

    public static AuthMember fromClaims(Claims claims) {
        UserRole role = UserRole.valueOf(claims.get("userRole", String.class));

        return new AuthMember(
                Long.valueOf(claims.getSubject()), // memberId
                claims.get("email", String.class),
                claims.get("name", String.class),
                claims.get("phoneNumber", String.class),
                claims.get("address", String.class),
                role, // 단일 권한 생성자 호출
                claims.get("provider", String.class)
        );
    }

    public UserRole getUserRole() {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(UserRole::valueOf)
                .findFirst()
                .orElseThrow(() -> new CustomException(HttpStatus.FORBIDDEN, "사용자 권한이 유효하지 않습니다."));
    }
}