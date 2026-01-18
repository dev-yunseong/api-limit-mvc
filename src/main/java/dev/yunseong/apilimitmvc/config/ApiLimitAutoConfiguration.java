package dev.yunseong.apilimitmvc.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import dev.yunseong.apilimitmvc.domain.LimitRule;
import dev.yunseong.apilimitmvc.filter.ApiLimitFilter;
import dev.yunseong.apilimitmvc.storage.InMemoryRateLimitStorage;
import dev.yunseong.apilimitmvc.storage.RateLimitStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(ApiLimitProperties.class)
@EnableScheduling
@RequiredArgsConstructor
public class ApiLimitAutoConfiguration {

    private final ApiLimitProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public RateLimitStorage<Object> rateLimitStorage() {
        log.info("Creating InMemoryRateLimitStorage bean");
        return new InMemoryRateLimitStorage<>();
    }

    @Bean
    @ConditionalOnMissingBean
    public ApiLimitFilter apiLimitFilter(
            ObjectProvider<LimitRule<?>> ruleProvider,
            RateLimitStorage<Object> storage
    ) {
        log.info("Creating ApiLimitFilter bean");

        List<LimitRule<?>> yamlRules = properties.getRules().stream()
                .map(ApiLimitProperties.Rule::toDomain)
                .collect(Collectors.toList());

        List<LimitRule<?>> customRules = ruleProvider.orderedStream().toList();

        List<LimitRule<?>> allRules = Stream.concat(yamlRules.stream(), customRules.stream())
                .collect(Collectors.toList());

        log.info("Total {} rules loaded (YAML: {}, Custom: {})",
                allRules.size(), yamlRules.size(), customRules.size());

        return new ApiLimitFilter(allRules, storage);
    }
}
