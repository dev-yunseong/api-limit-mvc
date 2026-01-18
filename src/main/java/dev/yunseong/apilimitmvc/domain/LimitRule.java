package dev.yunseong.apilimitmvc.domain;


import java.time.Duration;

import lombok.AllArgsConstructor;

import lombok.Getter;

@AllArgsConstructor
@Getter
public class LimitRule<T> {

    String path;
    Integer limit;
    Duration duration;
    Factor<T> factor;
}