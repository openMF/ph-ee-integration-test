package org.mifos.integrationtest.cucumber.stepdef;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.google.common.truth.Truth.assertThat;
import static org.mifos.integrationtest.common.Utils.getDefaultSpec;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSender;
import org.mifos.integrationtest.common.HttpMethod;
import org.springframework.beans.factory.annotation.Value;

public class MockServerStepDef extends BaseStepDef {

    @Value("${mock-server.port}")
    private int mockServerPortFromConfig;

    @Given("I can inject MockServer")
    public void checkIfMockServerIsInjected() {
        assertThat(mockServer).isNotNull();
    }

    @Then("I should be able to get instance of mock server")
    public void getInstanceOfMockServer() throws InterruptedException {
        assertThat(mockServer.getMockServer()).isNotNull();
        assertThat(mockServer.getMockServer().port()).isEqualTo(mockServerPortFromConfig);
    }

    @ParameterType(name = "httpMethod", value = ".*")
    public HttpMethod httpMethod(String httpMethod) {
        httpMethod = httpMethod.replace("\"", "");
        logger.debug("HTTP METHOD: $$$$$$: {}", httpMethod);
        return HttpMethod.valueOf(httpMethod);
    }

    @And("I can register the stub with {string} endpoint for {httpMethod} request with status of {int}")
    public void startStub(String endpoint, HttpMethod httpMethod, int status) {
        switch (httpMethod) {
            case GET -> {
                stubFor(get(endpoint).willReturn(status(status)));
            }
            case POST -> {
                stubFor(post(endpoint).willReturn(status(status)));
            }
            case PUT -> {
                stubFor(put(endpoint).willReturn(status(status)));
            }
            case DELETE -> {
                stubFor(delete(endpoint).willReturn(status(status)));
            }
        }
    }

    @When("I make the {httpMethod} request to {string} endpoint with expected status of {int}")
    public void hitStubEndpoint(HttpMethod httpMethod, String endpoint, int expectedStatus) {
        RequestSender requestSender = RestAssured.given(getDefaultSpec()).baseUri(mockServer.getBaseUri()).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when();

        switch (httpMethod) {
            case GET -> {
                requestSender.get(endpoint);
            }
            case POST -> {
                requestSender.post(endpoint).andReturn().asString();
            }
            case PUT -> {
                requestSender.put(endpoint);
            }
            case DELETE -> {
                requestSender.delete(endpoint);
            }
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Then("I should be able to verify that the {httpMethod} method to {string} endpoint received {int} request")
    public void verifyStub(HttpMethod httpMethod, String endpoint, int numberOfRequest) {
        switch (httpMethod) {
            case GET -> {
                verify(numberOfRequest, getRequestedFor(urlEqualTo(endpoint)));
            }
            case POST -> {
                verify(numberOfRequest, postRequestedFor(urlEqualTo(endpoint)));
            }
            case PUT -> {
                verify(numberOfRequest, putRequestedFor(urlEqualTo(endpoint)));
            }
            case DELETE -> {
                verify(numberOfRequest, deleteRequestedFor(urlEqualTo(endpoint)));
            }
        }
    }

    @And("I can start mock server")
    public void startMockServer() {
        mockServer.getMockServer().start();
        configureFor("localhost", mockServer.getMockServer().port());
    }

    @And("I can stop mock server")
    public void stopMockServer() {
        mockServer.getMockServer().stop();
    }
}
