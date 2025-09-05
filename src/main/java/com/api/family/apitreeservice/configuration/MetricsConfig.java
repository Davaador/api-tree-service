package com.api.family.apitreeservice.configuration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    public Counter customerCreatedCounter(MeterRegistry registry) {
        return Counter.builder("customer.created")
                .description("Number of customers created")
                .register(registry);
    }

    @Bean
    public Counter customerUpdatedCounter(MeterRegistry registry) {
        return Counter.builder("customer.updated")
                .description("Number of customers updated")
                .register(registry);
    }

    @Bean
    public Counter customerDeletedCounter(MeterRegistry registry) {
        return Counter.builder("customer.deleted")
                .description("Number of customers deleted")
                .register(registry);
    }

    @Bean
    public Counter authenticationSuccessCounter(MeterRegistry registry) {
        return Counter.builder("auth.success")
                .description("Number of successful authentications")
                .register(registry);
    }

    @Bean
    public Counter authenticationFailureCounter(MeterRegistry registry) {
        return Counter.builder("auth.failure")
                .description("Number of failed authentications")
                .register(registry);
    }

    @Bean
    public Counter rateLimitExceededCounter(MeterRegistry registry) {
        return Counter.builder("rate.limit.exceeded")
                .description("Number of rate limit violations")
                .register(registry);
    }

    @Bean
    public Timer customerServiceTimer(MeterRegistry registry) {
        return Timer.builder("customer.service.duration")
                .description("Customer service method execution time")
                .register(registry);
    }

    @Bean
    public Timer authServiceTimer(MeterRegistry registry) {
        return Timer.builder("auth.service.duration")
                .description("Authentication service method execution time")
                .register(registry);
    }
}
