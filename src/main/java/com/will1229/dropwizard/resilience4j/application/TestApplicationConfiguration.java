package com.will1229.dropwizard.resilience4j.application;

import com.will1229.dropwizard.resilience4j.configuration.Resilience4jConfiguration;
import io.dropwizard.Configuration;
import lombok.Getter;

@Getter
public class TestApplicationConfiguration extends Configuration {
    private Resilience4jConfiguration resilience4j;
}
