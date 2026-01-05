package com.uge.ws;

import com.uge.ws.config.RestConfig;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.io.IOException;
import java.net.URI;

public class BikeApplicationServer {

    public static final String BASE_URI = "http://0.0.0.0:8080/api/";

    public static void main(String[] args) throws IOException {
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
                URI.create(BASE_URI),
                new RestConfig()   // 👈 important
        );
        System.out.println("Server started at " + BASE_URI);
        System.out.println("Press CTRL+C to stop.");
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
        try {
            Thread.currentThread().join();
        } catch (InterruptedException ignored) {
        }
    }
}
