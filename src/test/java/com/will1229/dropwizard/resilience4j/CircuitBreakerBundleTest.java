package com.will1229.dropwizard.resilience4j;

import com.will1229.dropwizard.resilience4j.application.TestApplication;
import com.will1229.dropwizard.resilience4j.application.TestApplicationConfiguration;
import com.will1229.dropwizard.resilience4j.client.TestApplicationClient;
import com.will1229.dropwizard.resilience4j.configuration.CircuitBreakerConfiguration;
import com.will1229.dropwizard.resilience4j.configuration.Resilience4jConfiguration;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.vavr.collection.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.time.Duration;
import java.util.List;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.jetty.http.HttpStatus.INTERNAL_SERVER_ERROR_500;
import static org.eclipse.jetty.http.HttpStatus.NOT_FOUND_404;

public class CircuitBreakerBundleTest {

    private Resilience4jConfiguration r4jConfig;

    private static final String CIRCUIT_BREAKER_1 = "circuitBreaker1";
    private static final String FULL_CONFIG = "fullConfig";
    private static final String EMPTY_CONFIG = "emptyConfig";


    private final TestApplicationClient testApplicationClient = new TestApplicationClient("http://localhost:8080/app");

    @ClassRule
    public static final DropwizardAppRule<TestApplicationConfiguration> RULE = new DropwizardAppRule<>(
            TestApplication.class,
            resourceFilePath("test.yml"));

    @Before
    public void before() {
        r4jConfig = RULE.getConfiguration().getResilience4j();
    }

    @Test
    public void goThroughConfig() {
        assertThat(r4jConfig.getCircuitBreakers()).isNotNull();
        assertThat(r4jConfig.getCircuitBreakers().size()).isEqualTo(3);

        final CircuitBreakerConfiguration breaker1 = r4jConfig.getCircuitBreakers().get(0);
        assertThat(breaker1.getInstance()).isEqualTo(CIRCUIT_BREAKER_1);

        final CircuitBreakerConfiguration fullConfig = r4jConfig.getCircuitBreakers().get(1);
        assertThat(fullConfig.getInstance()).isEqualTo(FULL_CONFIG);
        assertThat(fullConfig.getEventConsumerBufferSize()).isEqualTo(10);
        assertThat(fullConfig.getFailureRateThreshold()).isEqualTo(50);
        assertThat(fullConfig.getMinimumNumberOfCalls()).isEqualTo(20);
        assertThat(fullConfig.getPermittedNumberOfCallsInHalfOpenState()).isEqualTo(3);
//        assertThat(fullConfig.getRecordFailurePredicate()).isEqualTo(CIRCUIT_BREAKER_1);
        assertThat(fullConfig.getSlidingWindowSize()).isEqualTo(10);
        assertThat(fullConfig.getSlidingWindowType()).isEqualTo("TIME_BASED");
        assertThat(fullConfig.getWaitDurationInOpenState()).isEqualTo(Duration.ofSeconds(50));

        assertThat(r4jConfig.getCircuitBreakers().get(2).getInstance()).isEqualTo(EMPTY_CONFIG);

        List<Pair<String, CircuitBreakerConfig.Builder>> circuitBreakers = ((TestApplication) RULE.getApplication()).getBreakersSeenInConfiguration();
        assertThat(circuitBreakers.size()).isEqualTo(3);
        assertThat(circuitBreakers.get(0).getKey()).isEqualTo(CIRCUIT_BREAKER_1);
        assertThat(circuitBreakers.get(1).getKey()).isEqualTo(FULL_CONFIG);
        assertThat(circuitBreakers.get(2).getKey()).isEqualTo(EMPTY_CONFIG);

        assertThat(r4jConfig.getCircuitBreakerRegistry().circuitBreaker(CIRCUIT_BREAKER_1)).isNotNull();
        assertThat(r4jConfig.getCircuitBreakerRegistry().circuitBreaker(FULL_CONFIG)).isNotNull();
        assertThat(r4jConfig.getCircuitBreakerRegistry().circuitBreaker(EMPTY_CONFIG)).isNotNull();
    }

    @Test
    public void circuitBreakerExample() throws InterruptedException {
        Stream.range(0, 5).forEach(i -> assertThat(testApplicationClient.getResultWithCircuitBreaker().getStatus()).isEqualTo(NOT_FOUND_404));
        assertThat(testApplicationClient.getResultWithCircuitBreaker().getStatus()).isEqualTo(INTERNAL_SERVER_ERROR_500);
        System.out.println("Waiting for circuit to close...");
        Thread.sleep(1100);
        assertThat(testApplicationClient.getResultWithCircuitBreaker().getStatus()).isEqualTo(NOT_FOUND_404);
    }
}
