package org.mifos.integrationtest.cucumber.stepdef;

import com.google.gson.Gson;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.common.Utils;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;

import static com.google.common.truth.Truth.assertThat;

public class AuthStepDef extends BaseStepDef {

    @Value("${operations-app.auth.enabled}")
    public Boolean authEnabled;

    @Value("${operations-app.auth.header}")
    public String authHeader;

    @When("I call the auth endpoint with username: {string} and password: {string}")
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
}
