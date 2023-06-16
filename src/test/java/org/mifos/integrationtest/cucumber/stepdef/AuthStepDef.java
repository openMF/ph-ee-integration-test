package org.mifos.integrationtest.cucumber.stepdef;

import com.google.gson.Gson;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.KeycloakTokenResponse;
import org.mifos.integrationtest.config.KeycloakConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import static org.mifos.integrationtest.common.Utils.CONTENT_TYPE;

import java.util.HashMap;

import static com.google.common.truth.Truth.assertThat;

public class AuthStepDef extends BaseStepDef {

    @Value("${operations-app.auth.enabled}")
    public Boolean authEnabled;

    @Value("${operations-app.auth.header}")
    public String authHeader;

    @Autowired
    KeycloakConfig keycloakConfig;

    @When("I call the operations-app auth endpoint with username: {string} and password: {string}")
    public void authenticateWithUsernameAndPassword(String username, String password) {
        if (authEnabled) {
            RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
            requestSpec.header("Authorization", authHeader);
            requestSpec.queryParam("username", username);
            requestSpec.queryParam("password", password);
            requestSpec.queryParam("grant_type", "password");

            BaseStepDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).body("{}").expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().post(operationsAppConfig.authEndpoint).andReturn()
                    .asString();
        }

    }

    @Then("I should get a valid token")
    public void checkToken() {
        if (authEnabled) {
            HashMap<String, Object> authResponse = new Gson().fromJson(BaseStepDef.response, HashMap.class);
            String token;
            try {
                token = (String) authResponse.get("access_token");
            } catch (Exception e) {
                token = null;
            }
            assertThat(token).isNotEmpty();
            BaseStepDef.accessToken = token;
            logger.info("Access token: " + BaseStepDef.accessToken);
        }
    }

    @When("I call the auth endpoint")
    public void iCallTheAuthEndpoint() {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        requestSpec.header("Authorization", "Basic Y2xpZW50Og==");
        requestSpec.queryParam("username", operationsAppConfig.username);
        requestSpec.queryParam("password", operationsAppConfig.password);
        requestSpec.queryParam("grant_type", "password");

        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).body("{}").expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().post(operationsAppConfig.authEndpoint).andReturn()
                .asString();
    }

    @When("I call the keycloak auth api with {string} username and {string} password")
    public void getTokenFromKeycloakUser(String username, String password) throws JsonProcessingException {
        RequestSpecification requestSpecification = Utils.getDefaultSpec();
        requestSpecification.header(CONTENT_TYPE, "application/x-www-form-urlencoded");
        requestSpecification
                .formParam(KeycloakConfig.headerUsernameKey, username)
                .formParam(KeycloakConfig.headerPasswordKey, password)
                .formParam(KeycloakConfig.headerClientIdKey, keycloakConfig.clientId)
                .formParam(KeycloakConfig.headerClientSecretKey, keycloakConfig.clientSecret)
                .formParam(KeycloakConfig.headerGrantTypeKey, keycloakConfig.grantType);
        BaseStepDef.response = RestAssured.given(requestSpecification)
                .baseUri(keycloakConfig.keycloakContactPoint)
                .expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(keycloakConfig.tokenEndpoint)
                .andReturn().asString();
        try {
            BaseStepDef.keycloakTokenResponse = objectMapper.readValue(BaseStepDef.response, KeycloakTokenResponse.class);
        } catch (Exception e) {
            BaseStepDef.keycloakTokenResponse = null;
        }
        logger.info("97736366: {}", BaseStepDef.keycloakTokenResponse.getAccessToken());
        assertThat(BaseStepDef.keycloakTokenResponse).isNotNull();
        assertThat(BaseStepDef.keycloakTokenResponse.getAccessToken()).isNotNull();
    }
}
