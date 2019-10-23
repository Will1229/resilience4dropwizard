package com.will1229.dropwizard.resilience4j.configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
public class Resilience4jConfiguration {

    @Nullable
    @Valid
    public List<CircuitBreakerConfiguration> circuitBreakers;

    public CircuitBreakerRegistry circuitBreakerRegistry;
}
