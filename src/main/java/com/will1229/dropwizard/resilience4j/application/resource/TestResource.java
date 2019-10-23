package com.will1229.dropwizard.resilience4j.application.resource;


import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.vavr.control.Try;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.function.Supplier;

@Path("test")
@Produces(MediaType.APPLICATION_JSON)
public class TestResource {

    private final CircuitBreaker circuitBreaker;

    public TestResource(final CircuitBreaker circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }

    @GET
    @Path("circuit-breaker")
    public String testCircuitBreaker() {
        Supplier<String> supplier = CircuitBreaker.decorateSupplier(circuitBreaker, this::notFound);
        return Try.ofSupplier(supplier).get();
    }

    private String notFound() {
        throw new NotFoundException("BAM!");
    }
}
