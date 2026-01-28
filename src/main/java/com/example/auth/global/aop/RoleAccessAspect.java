package com.example.auth.global.aop;

import com.example.auth.global.annotation.UserRole;
import com.example.auth.global.exception.CustomException;
import com.example.auth.global.response.AuthMember;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RoleAccessAspect {

    @Before("@annotation(org.example.global.annotation.Admin)")
    public void checkAdminAccess() {
        checkRole(UserRole.ROLE_ADMIN, "관리자 권한이 필요합니다.");
    }

    @Before("@annotation(org.example.global.annotation.Member)")
    public void checkMemberAccess() {
        checkAuthenticated("로그인이 필요합니다.");
    }

    private void checkAuthenticated(String errorMessage) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, errorMessage);
        }
    }

    private void checkRole(UserRole requiredRole, String errorMessage) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        AuthMember authMember = (AuthMember) authentication.getPrincipal();
        UserRole role = authMember.getUserRole();

        // ADMIN은 USER 권한 포함
        if (requiredRole == UserRole.ROLE_ADMIN && role != UserRole.ROLE_ADMIN) {
            throw new CustomException(HttpStatus.FORBIDDEN, errorMessage);
        }
    }
}


