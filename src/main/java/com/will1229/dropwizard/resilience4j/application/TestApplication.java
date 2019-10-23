package com.will1229.dropwizard.resilience4j.application;

import com.will1229.dropwizard.resilience4j.application.resource.TestResource;
import com.will1229.dropwizard.resilience4j.bundle.CircuitBreakerBundle;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class TestApplication extends Application<TestApplicationConfiguration> {

    private List<Pair<String, CircuitBreakerConfig.Builder>> breakersSeenInConfiguration = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        new TestApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<TestApplicationConfiguration> bootstrap) {
        CircuitBreakerBundle<TestApplicationConfiguration> bundle = new CircuitBreakerBundle<>(TestApplicationConfiguration::getResilience4j, this::addBreakerConfigurationToTestList);
        bootstrap.addBundle(bundle);
    }

    @Override
    public void run(TestApplicationConfiguration configuration, Environment environment) {
        final CircuitBreaker circuitBreaker = configuration.getResilience4j().getCircuitBreakerRegistry().circuitBreaker("circuitBreaker1");
        environment.jersey().register(new TestResource(circuitBreaker));
    }

    private void addBreakerConfigurationToTestList(String key, CircuitBreakerConfig.Builder builder) {
        breakersSeenInConfiguration.add(Pair.of(key, builder));
    }

    public List<Pair<String, CircuitBreakerConfig.Builder>> getBreakersSeenInConfiguration() {
        return breakersSeenInConfiguration;
    }
}
