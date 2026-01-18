package dev.yunseong.apilimitmvc.storage;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.scheduling.annotation.Scheduled;

public class InMemoryRateLimitStorage<T> implements RateLimitStorage<T> {

    private final ConcurrentHashMap<T, List<Long>> storage = new ConcurrentHashMap<>();

    @Override
    public synchronized boolean isAllowed(T key, int limit, Duration duration) {
        List<Long> timestamps = storage.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>());
        long now = Instant.now().toEpochMilli();

        timestamps.removeIf(timestamp -> now - timestamp > duration.toMillis());

        if (timestamps.size() < limit) {
            timestamps.add(now);
            return true;
        }

        return false;
    }

    @Scheduled(fixedRate = 1000 * 60 * 10)
    public void clearOldEntries() {
        storage.forEach((key, timestamps) -> {
            long now = Instant.now().toEpochMilli();
            timestamps.removeIf(timestamp -> now - timestamp > Duration.ofHours(1).toMillis());
            if (timestamps.isEmpty()) {
                storage.remove(key);
            }
        });
    }
}
