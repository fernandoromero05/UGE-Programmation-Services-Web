package com.uge.ws.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

public class RestConfig extends ResourceConfig {

    public RestConfig() {
        // Register our resource packages
        packages(
                "com.uge.ws.corp",
                "com.uge.ws.shop",
                "com.uge.ws.bank"
        );

        // Enable Jackson JSON support
        register(JacksonFeature.class);
    }
}
