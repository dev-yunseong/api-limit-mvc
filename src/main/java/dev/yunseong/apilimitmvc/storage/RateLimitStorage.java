package dev.yunseong.apilimitmvc.storage;

import java.time.Duration;

public interface RateLimitStorage<T> {
    boolean isAllowed(T key, int limit, Duration duration);
}
