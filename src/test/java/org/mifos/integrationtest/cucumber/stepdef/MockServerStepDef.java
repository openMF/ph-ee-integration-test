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
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
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
import org.mifos.integrationtest.config.WireMockServerSingleton;
import org.springframework.beans.factory.annotation.Value;

public class MockServerStepDef extends BaseStepDef {

    private static Boolean wiremockStarted = false;

    @Value("${mock-server.port}")
    private int mockServerPortFromConfig;

    @Given("I can inject MockServer")
    public void checkIfMockServerIsInjected() {
        assertThat(WireMockServerSingleton.getInstance()).isNotNull();
    }

    @Then("I should be able to get instance of mock server")
    public void getInstanceOfMockServer() throws InterruptedException {
        assertThat(WireMockServerSingleton.getInstance()).isNotNull();
        assertThat(WireMockServerSingleton.getPort()).isEqualTo(mockServerPortFromConfig);
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
                WireMockServerSingleton.getInstance().stubFor(get(urlPathMatching(endpoint)).willReturn(status(status)));
            }
            case POST -> {
                WireMockServerSingleton.getInstance().stubFor(post(urlPathMatching(endpoint)).willReturn(status(status)));
            }
            case PUT -> {
                WireMockServerSingleton.getInstance().stubFor(put(urlPathMatching(endpoint)).willReturn(status(status)));
            }
            case DELETE -> {
                WireMockServerSingleton.getInstance().stubFor(delete(urlPathMatching(endpoint)).willReturn(status(status)));
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
                requestSender.put(endpoint).andReturn().asString();
            }
            case DELETE -> {
                requestSender.delete(endpoint);
            }
        }
    }

    @Then("I should be able to verify that the {httpMethod} method to {string} endpoint received {int} request")
    public void verifyStub(HttpMethod httpMethod, String endpoint, int numberOfRequest) {
        await().atMost(awaitMost, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            switch (httpMethod) {
                case GET -> {
                    WireMockServerSingleton.getInstance().verify(numberOfRequest, getRequestedFor(urlEqualTo(endpoint)));
                }
                case POST -> {
                    WireMockServerSingleton.getInstance().verify(numberOfRequest, postRequestedFor(urlEqualTo(endpoint)));
                }
                case PUT -> {
                    WireMockServerSingleton.getInstance().verify(numberOfRequest, putRequestedFor(urlEqualTo(endpoint)));
                }
                case DELETE -> {
                    WireMockServerSingleton.getInstance().verify(numberOfRequest, deleteRequestedFor(urlEqualTo(endpoint)));
                }
            }
        });
    }

    @And("I can start mock server")
    public void startMockServer() {
        WireMockServerSingleton.getInstance().start();
        configureFor("localhost", WireMockServerSingleton.getPort());
    }

    @And("I can stop mock server")
    public void stopMockServer() {
        WireMockServerSingleton.getInstance().stop();
    }

    @Given("I will start the mock server")
    public void iWillStartTheMockServer() {
        if (!wiremockStarted) {
            checkIfMockServerIsInjected();
            startMockServer();
        }
    }

    @And("I will register the stub with {string} endpoint for {httpMethod} request with status of {int}")
    public void iWillRegisterTheStubWithEndpointForRequestWithStatusOf(String endpoint, HttpMethod httpMethod, int status) {
        if (!wiremockStarted) {
            startStub(endpoint, httpMethod, status);

        }
    }

    @Then("I will update the  mock server and register stub as done")
    public void iWillUpdateTheMockServerAndRegisterStubAsDone() {
        wiremockStarted = true;
    }
}
