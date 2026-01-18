package dev.yunseong.apilimitmvc.domain;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class IPFactor implements Factor<String> {

    public String getKey(HttpServletRequest request, HttpServletResponse response) {
        return request.getRemoteAddr();
    }
}