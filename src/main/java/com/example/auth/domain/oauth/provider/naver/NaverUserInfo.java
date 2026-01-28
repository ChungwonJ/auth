package com.example.auth.domain.oauth.provider.naver;

import com.example.auth.domain.oauth.user.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class NaverUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getEmail() {
        return (String) ((Map) attributes.get("response")).get("email");
    }

    @Override
    public String getName() {
        return (String) ((Map) attributes.get("response")).get("name");
    }

    @Override
    public String getPhone() {
        return (String) ((Map) attributes.get("response")).get("mobile");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

}
