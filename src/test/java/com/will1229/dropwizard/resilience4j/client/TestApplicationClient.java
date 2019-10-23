package com.will1229.dropwizard.resilience4j.client;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class TestApplicationClient {
    private final String base;

    public TestApplicationClient(final String base) {
        this.base = base;
    }

    public Response getResultWithCircuitBreaker() {
        final WebTarget target = ClientBuilder.newClient().target(base);
        return target.path("test/circuit-breaker").request().get();
    }
}
