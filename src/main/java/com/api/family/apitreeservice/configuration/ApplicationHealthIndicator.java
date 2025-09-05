package com.api.family.apitreeservice.configuration;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ApplicationHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Add custom health checks here
        return Health.up()
                .withDetail("application", "api-tree-service")
                .withDetail("status", "Running")
                .withDetail("version", "1.0.0")
                .build();
    }
}
