package org.mifos.integrationtest.cucumber;

import com.google.gson.Gson;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.common.Utils;
import java.util.HashMap;

import static com.google.common.truth.Truth.assertThat;

public class AuthStepDef extends BaseStepDef {

    @When("I call the auth endpoint with username: {string} and password: {string}")
    public void authenticateWithUsernameAndPassword(String username, String password) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        requestSpec.header("Authorization", "Basic Y2xpZW50Og==");
        requestSpec.queryParam("username", username);
        requestSpec.queryParam("password", password);
        requestSpec.queryParam("grant_type", "password");

        BaseStepDef.response = RestAssured.given(requestSpec)
                .baseUri(operationsAppConfig.operationAppContactPoint)
                .body("{}")
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(operationsAppConfig.authEndpoint)
                .andReturn().asString();
    }

    @Then("I should get a valid token")
    public void checkToken() {
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
