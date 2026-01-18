package dev.yunseong.apilimitmvc.filter;

import dev.yunseong.apilimitmvc.domain.LimitRule;
import dev.yunseong.apilimitmvc.storage.RateLimitStorage;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ApiLimitFilter implements Filter {

    private final List<LimitRule<?>> rules;
    private final RateLimitStorage<Object> rateLimitStorage;
    private final PathPatternParser pathParser = new PathPatternParser();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestPath = request.getRequestURI();
        log.debug("Request path: {}", requestPath);

        List<LimitRule<?>> matchedRules = rules.stream()
                .filter(rule -> {
                    log.debug("Matching rule path: {}", rule.getPath());
                    PathPattern pattern = pathParser.parse(rule.getPath());
                    return pattern.matches(PathContainer.parsePath(request.getServletPath()));
                })
                .collect(Collectors.toList());

        log.debug("Matched rules: {}", matchedRules);

        if (isAllowed(request, matchedRules)) {
            log.debug("Request allowed");
            filterChain.doFilter(request, response);
        } else {
            log.warn("Request blocked");
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        }
    }

    private boolean isAllowed(HttpServletRequest request, List<LimitRule<?>> rules) {
        boolean fullyAllowed = true;

        for (LimitRule<?> rule : rules) {
            Object key = rule.getFactor().getKey(request);
            boolean isAllowed = rateLimitStorage.isAllowed(
                    key,
                    rule.getLimit(),
                    rule.getDuration()
            );
            log.debug("Rule: {}, Key: {}, Allowed: {}", rule, key, isAllowed);

            fullyAllowed = fullyAllowed && isAllowed;
        }

        return fullyAllowed;
    }
}
