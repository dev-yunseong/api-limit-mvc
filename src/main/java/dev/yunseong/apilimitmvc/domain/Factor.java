package dev.yunseong.apilimitmvc.domain;

import jakarta.servlet.http.HttpServletRequest;

public interface Factor<T> {

    T getKey(HttpServletRequest request);
}