package dev.yunseong.apilimitmvc.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import dev.yunseong.apilimitmvc.domain.IPFactor;
import dev.yunseong.apilimitmvc.domain.Factor;
import dev.yunseong.apilimitmvc.domain.LimitRule;
import lombok.Getter;

@ConfigurationProperties("api-limit")
public class ApiLimitProperties {

    @Getter
    private List<Rule> rules = new ArrayList<>();

    record Rule(
            String path,
            Integer limit,         // 허용 횟수
            Duration duration,    // 기간 (예: 1m, 1h)
            LimitFactor factor  // 제한 기준 (IP, HEADER 등)
    ) {

        public LimitRule<?> toDomain() {
            return new LimitRule<>(path, limit, duration, factor.getDomain());
        }
    }

    enum LimitFactor {
        IP(new IPFactor());

        @Getter
        private final Factor<?> domain;

        LimitFactor(Factor<?> factor) {
            this.domain = factor;
        }
    }
}