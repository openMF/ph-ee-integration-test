package org.mifos.integrationtest.config;

import com.github.tomakehurst.wiremock.WireMockServer;

public interface MockServer {

    public WireMockServer getMockServer();
}
