package com.will1229.dropwizard.resilience4j.configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import lombok.Getter;

import java.time.Duration;

@Getter
public class CircuitBreakerConfiguration {

    private String instance = "default";

    private float failureRateThreshold = 50;

    private Duration waitDurationInOpenState = Duration.ofSeconds(60);

    private boolean enableAutomaticTransitionFromOpenToHalfOpen = true;

    private boolean registerHealthIndicator = true;

    private int slidingWindowSize = 10;

    private int permittedNumberOfCallsInHalfOpenState = 3;

    private String slidingWindowType = "TIME_BASED";

    private int minimumNumberOfCalls = 20;

    private int eventConsumerBufferSize = 10;

    private String recordFailurePredicate = "io.github.robwin.exception.RecordFailurePredicate";


    public CircuitBreakerConfig.Builder toResilience4jConfigBuilder() {
        CircuitBreakerConfig.Builder builder = CircuitBreakerConfig.custom()
                .failureRateThreshold(failureRateThreshold)
                .slidingWindowSize(slidingWindowSize)
                .permittedNumberOfCallsInHalfOpenState(permittedNumberOfCallsInHalfOpenState)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.valueOf(slidingWindowType))
                .minimumNumberOfCalls(minimumNumberOfCalls)
                .waitDurationInOpenState(waitDurationInOpenState)
                .failureRateThreshold(failureRateThreshold);
        if (enableAutomaticTransitionFromOpenToHalfOpen) {
            builder.enableAutomaticTransitionFromOpenToHalfOpen();
        }
        return builder;
    }
}
