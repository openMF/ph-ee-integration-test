package org.mifos.integrationtest.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.FatalStartupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WireMockServerSingleton {

    static Logger logger = LoggerFactory.getLogger(WireMockServerSingleton.class);
    private static final ThreadLocal<WireMockServer> threadLocalInstance = new ThreadLocal<>();

    public static WireMockServer getInstance() {
        WireMockServer instance = threadLocalInstance.get();
        if (instance == null || !instance.isRunning()) {
            synchronized (WireMockServerSingleton.class) {
                instance = threadLocalInstance.get(); // Double-check idiom
                if (instance == null || !instance.isRunning()) {
                    instance = startWireMockServerWithRetry(3); // Retry 3 times
                    threadLocalInstance.set(instance);
                }
            }
        }
        return instance;
    }

    private static WireMockServer startWireMockServerWithRetry(int maxRetries) {
        WireMockServer server = null;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            int port = getRandomPort();
            server = new WireMockServer(port);
            try {
                server.start();
                logger.info("WireMock started on port {}", server.port());
                return server;
            } catch (FatalStartupException e) {
                logger.error("Failed to start WireMock on port {} (Attempt {}/{}). Retrying...", port, attempt, maxRetries, e);
                // Optionally, add a short delay here if needed
            }
        }
        throw new IllegalStateException("Failed to start WireMock server after " + maxRetries + " attempts.");
    }

    private static int getRandomPort() {
        // This returns a port number in the range 1024-65535
        return 1024 + (int) (Math.random() * ((65535 - 1024) + 1));
    }

    public static int getPort() {
        WireMockServer instance = threadLocalInstance.get();
        if (instance != null && instance.isRunning()) {
            return instance.port();
        } else {
            throw new IllegalStateException("WireMock server is not running.");
        }
    }
}
