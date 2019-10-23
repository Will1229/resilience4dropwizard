package com.will1229.dropwizard.resilience4j.bundle;

import com.will1229.dropwizard.resilience4j.configuration.CircuitBreakerConfiguration;
import com.will1229.dropwizard.resilience4j.configuration.Resilience4jConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.internal.InMemoryCircuitBreakerRegistry;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CircuitBreakerBundle<T extends Configuration> implements ConfiguredBundle<T> {

    private final Function<T, Resilience4jConfiguration> resiliencyConfiguratorFunction;

    private final BiConsumer<String, CircuitBreakerConfig.Builder> circuitBreakerConfigurator;

    public CircuitBreakerBundle(@Nonnull Function<T, Resilience4jConfiguration> resilienceConfiguratorFunction,
                                @Nonnull BiConsumer<String, CircuitBreakerConfig.Builder> circuitBreakerConfigurator) {
        this.resiliencyConfiguratorFunction = resilienceConfiguratorFunction;
        this.circuitBreakerConfigurator = circuitBreakerConfigurator;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(T dwConfig, Environment environment) {
        Resilience4jConfiguration config = resiliencyConfiguratorFunction.apply(dwConfig);

        List<CircuitBreakerConfiguration> circuitBreakers = config.getCircuitBreakers();
        if (circuitBreakers != null && !circuitBreakers.isEmpty()) {
            InMemoryCircuitBreakerRegistry breakerRegistry = new InMemoryCircuitBreakerRegistry();
            for (CircuitBreakerConfiguration cfg : circuitBreakers) {
                CircuitBreakerConfig.Builder r4jConfigBuilder = cfg.toResilience4jConfigBuilder();
                circuitBreakerConfigurator.accept(cfg.getInstance(), r4jConfigBuilder);
                breakerRegistry.circuitBreaker(cfg.getInstance(), r4jConfigBuilder.build());
            }
            config.setCircuitBreakerRegistry(breakerRegistry);
        }
    }
}
