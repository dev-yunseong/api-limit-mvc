package dev.yunseong.apilimitmvc.domain;

import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

public class CookieFactor implements Factor<String> {

    private final   String API_LIMIT_KEY = "api_limit";

    @Override
    public String getKey(HttpServletRequest request, HttpServletResponse response) {

        String value  = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(API_LIMIT_KEY))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);


        if (value == null) {
            String rawUuid = UUID.randomUUID().toString();

            ResponseCookie cookie = ResponseCookie.from(API_LIMIT_KEY, rawUuid)
                    .path("/")
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .maxAge(Duration.ofDays(1))
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            value = rawUuid;
        }

        return value;
    }
}