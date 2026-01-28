package com.example.auth.domain.oauth.user;

import com.example.auth.domain.oauth.provider.google.GoogleUserInfo;
import com.example.auth.domain.oauth.provider.kakao.KakaoUserInfo;
import com.example.auth.domain.oauth.provider.naver.NaverUserInfo;
import com.example.auth.global.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OAuth2UserInfoFactory {

    public OAuth2UserInfo from(String provider, Map<String, Object> attributes) {
        return switch (provider.toLowerCase()) {
            case "google" -> new GoogleUserInfo(attributes);
            case "kakao" -> new KakaoUserInfo(attributes);
            case "naver" -> new NaverUserInfo(attributes);
            default -> throw new CustomException(HttpStatus.BAD_REQUEST, "지원하지 않는 로그인 서비스입니다.");
        };
    }

}
