package dev.yunseong.apilimitmvc.domain;

import jakarta.servlet.http.HttpServletRequest;

public class IPFactor implements Factor<String> {

    public String getKey(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}