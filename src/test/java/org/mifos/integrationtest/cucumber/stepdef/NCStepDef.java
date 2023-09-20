package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.config.NetflixConductorConfig;
import org.springframework.beans.factory.annotation.Autowired;
import static com.google.common.truth.Truth.assertThat;

public class NCStepDef {

    @Autowired
    NetflixConductorConfig netflixConductorConfig;

    @When("I make a call to nc server health API with expected status 200")
    public void ncHealthAPICall() {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(netflixConductorConfig.conductorServerContactPoint)
              .expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build())
              .when().get(netflixConductorConfig.healthEndpoint).andReturn().asString();
    }

    @Then("I get the value of Healthy as true in response")
    public void checkHealthyState()throws JSONException {
        JSONObject response = new JSONObject(BaseStepDef.response);
        assertThat(response).isNotNull();
        String healthStatus = response.getString("healthy");
        assertThat(healthStatus).isEqualTo("true");
    }
}
