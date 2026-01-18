package dev.yunseong.apilimitmvc.domain;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface Factor<T> {

    T getKey(HttpServletRequest request, HttpServletResponse response);
}