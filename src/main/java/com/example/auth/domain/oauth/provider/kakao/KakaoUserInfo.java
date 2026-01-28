package com.example.auth.domain.oauth.provider.kakao;

import com.example.auth.domain.oauth.user.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class KakaoUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        return (String) ((Map) attributes.get("kakao_account")).get("email");
    }

    @Override
    public String getName() {
        return (String) ((Map) attributes.get("properties")).get("nickname");
    }

    @Override
    public String getPhone() {
        return "";
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

}
