package com.example.auth.domain.auth.controller;


import com.example.auth.domain.auth.dto.request.SignInRequestDto;
import com.example.auth.domain.auth.dto.request.SignUpRequestDto;
import com.example.auth.domain.auth.dto.response.SignInResponseDto;
import com.example.auth.domain.auth.dto.response.SignUpResponseDto;
import com.example.auth.domain.auth.service.AuthService;
import com.example.auth.domain.auth.service.RefreshTokenService;
import com.example.auth.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<SignUpResponseDto>> signUp(
            @Valid @RequestBody SignUpRequestDto requestDto
    ) {
        return ResponseEntity.ok(ApiResponse.of(authService.signUp(requestDto)));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<SignInResponseDto>> signIn(
            @Valid @RequestBody SignInRequestDto requestDto,
            HttpServletResponse response
    ) {
        SignInResponseDto responseDto = authService.signIn(requestDto);

        // Refresh Token만 HttpOnly 쿠키에 저장 (Bearer 공백 제거하여 RFC2616 에러 방지)
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", responseDto.getRefreshToken().replace("Bearer ", ""))
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(7 * 24 * 60 * 60) // 7일
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        // Access Token은 Body로 반환 (Header용)
        return ResponseEntity.ok(ApiResponse.of(responseDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<SignInResponseDto>> refreshToken(
            @CookieValue(name = "refreshToken") String refreshToken
    ) {
        // 쿠키의 리프레시 토큰으로 갱신
        return ResponseEntity.ok(ApiResponse.of(refreshTokenService.refreshToken(refreshToken)));
    }

    @PostMapping("/sign-out")
    public ResponseEntity<ApiResponse<Void>> signOut(HttpServletResponse response) {
        // 쿠키 삭제 응답
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(ApiResponse.of(null));
    }
}
