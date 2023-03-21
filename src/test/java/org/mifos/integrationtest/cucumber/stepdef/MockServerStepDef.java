package org.mifos.integrationtest.cucumber.stepdef;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Value;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.google.common.truth.Truth.assertThat;

public class MockServerStepDef extends BaseStepDef {

    @Value("${mock-server.port}")
    private int mockServerPortFromConfig;

    @Given("I can inject MockServer")
    public void checkIfMockServerIsInjected() {
        assertThat(mockServer).isNotNull();
        mockServer.getMockServer().start();
        configureFor("localhost", mockServer.getMockServer().port());
    }

    @Then("I should be able to get instance of mock server")
    public void getInstanceOfMockServer() throws InterruptedException {
        assertThat(mockServer.getMockServer()).isNotNull();
        assertThat(mockServer.getMockServer().port()).isEqualTo(mockServerPortFromConfig);
    }

}
