// src/main/java/com/uge/ws/config/RestConfig.java
package com.uge.ws.config;

import org.glassfish.jersey.server.ResourceConfig;

public class RestConfig extends ResourceConfig {
    public RestConfig() {
        // Package scanning for resources
        packages("com.uge.ws.corp", "com.uge.ws.shop", "com.uge.ws.bank");
    }
}
