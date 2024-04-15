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
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mifos.integrationtest.common.Utils.getDefaultSpec;

import com.github.tomakehurst.wiremock.WireMockServer;
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

public class MockServerStepDef extends BaseStepDef {

    private static Boolean wiremockStarted = false;
    private WireMockServer wireMockServer;

    @Given("I can inject MockServer")
    public void checkIfMockServerIsInjected() {
        assertThat(WireMockServerSingleton.getInstance()).isNotNull();
        // logger.info("{}", mockServer.getMockServer().baseUrl());
    }

    @Then("I should be able to get instance of mock server")
    public void getInstanceOfMockServer() throws InterruptedException {
        // assertThat(mockServer.getMockServer()).isNotNull();
        // assertThat(mockServer.getMockServer().port()).isEqualTo(53013);
        // WireMockServerSingleton.getInstance
        assertThat(WireMockServerSingleton.getInstance()).isNotNull();
        // assertThat(WireMockServerSingleton.getInstance().port()).isEqualTo(53013);
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
                // wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(endpoint)).willReturn(WireMock.aResponse().withStatus(200)));
                WireMockServerSingleton.getInstance().stubFor(get(urlPathMatching(endpoint)).willReturn(status(status)));

            }
            case POST -> {
                // wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo(endpoint)).willReturn(WireMock.aResponse().withStatus(200)));
                WireMockServerSingleton.getInstance().stubFor(post(urlPathMatching(endpoint)).willReturn(status(status)));
                // configureFor("localhost",4040);
            }
            case PUT -> {
                // wireMockServer.stubFor(WireMock.put(WireMock.urlEqualTo(endpoint)).willReturn(WireMock.aResponse().withStatus(200)));
                WireMockServerSingleton.getInstance().stubFor(put(urlPathMatching(endpoint)).willReturn(status(status)));

                // mockServer.getMockServer().stubFor(put(urlPathMatching(endpoint)).willReturn(status(status)));
            }
            case DELETE -> {
                // wireMockServer.stubFor(WireMock.delete(WireMock.urlEqualTo(endpoint)).willReturn(WireMock.aResponse().withStatus(200)));
                WireMockServerSingleton.getInstance().stubFor(delete(urlPathMatching(endpoint)).willReturn(status(status)));
            }
        }
    }

    @When("I make the {httpMethod} request to {string} endpoint with expected status of {int}")
    public void hitStubEndpoint(HttpMethod httpMethod, String endpoint, int expectedStatus) {
        RequestSender requestSender = RestAssured.given(getDefaultSpec()).baseUri("http://localhost").expect()
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
        WireMockServerSingleton.getInstance();
        // mockServer.getMockServer().start();
        configureFor("localhost", WireMockServerSingleton.getInstance().port());
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
