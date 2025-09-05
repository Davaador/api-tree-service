package com.api.family.apitreeservice.configuration;

import com.api.family.apitreeservice.exception.CustomError;
import com.api.family.apitreeservice.exception.Errors;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter implements Filter {

    private final SecurityProperties securityProperties;
    private final ObjectMapper objectMapper;
    private final Counter rateLimitExceededCounter;

    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!securityProperties.getRateLimit().isEnabled()) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = getClientIpAddress(httpRequest);
        String key = clientIp + ":" + httpRequest.getRequestURI();

        RateLimitInfo rateLimitInfo = rateLimitMap.computeIfAbsent(key, k -> new RateLimitInfo());

        long currentTime = System.currentTimeMillis();

        // Reset counter if minute has passed
        if (currentTime - rateLimitInfo.getLastReset() > 60000) {
            rateLimitInfo.reset(currentTime);
        }

        int currentCount = rateLimitInfo.incrementAndGet();
        int limit = securityProperties.getRateLimit().getRequestsPerMinute();

        if (currentCount > limit) {
            log.warn("Rate limit exceeded for IP: {} on endpoint: {}", clientIp, httpRequest.getRequestURI());
            rateLimitExceededCounter.increment();

            CustomError error = Errors.RATE_LIMIT_EXCEEDED;
            error.setMessage("Rate limit exceeded. Maximum " + limit + " requests per minute allowed.");

            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpResponse.getWriter().write(objectMapper.writeValueAsString(error));
            return;
        }

        // Add rate limit headers
        httpResponse.setHeader("X-RateLimit-Limit", String.valueOf(limit));
        httpResponse.setHeader("X-RateLimit-Remaining", String.valueOf(limit - currentCount));
        httpResponse.setHeader("X-RateLimit-Reset", String.valueOf(rateLimitInfo.getLastReset() + 60000));

        chain.doFilter(request, response);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    private static class RateLimitInfo {
        private final AtomicInteger count = new AtomicInteger(0);
        private long lastReset;

        public int incrementAndGet() {
            return count.incrementAndGet();
        }

        public void reset(long currentTime) {
            count.set(0);
            lastReset = currentTime;
        }

        public long getLastReset() {
            return lastReset;
        }
    }
}
