package com.example.auth.global.jwt;

import com.example.auth.global.response.AuthMember;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private static final List<String> EXCLUDE_URI_PREFIXES = List.of("/api/v1/auth/");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (isExcludedUri(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = jwtUtil.substringToken(authHeader);
            try {
                Claims claims = jwtUtil.extractClaims(jwt);
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    AuthMember authMember = AuthMember.fromClaims(claims);
                    JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authMember);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (ExpiredJwtException e) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Access Token Expired");
                return;
            } catch (Exception e) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid Token");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isExcludedUri(String uri) {
        return EXCLUDE_URI_PREFIXES.stream().anyMatch(uri::startsWith);
    }
}