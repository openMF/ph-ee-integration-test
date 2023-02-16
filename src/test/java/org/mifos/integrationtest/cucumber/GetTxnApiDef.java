package org.mifos.integrationtest.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.common.Utils;
import org.springframework.beans.factory.annotation.Value;

import static com.google.common.truth.Truth.assertThat;



public class GetTxnApiDef extends BaseStepDef {

    @Value("${operations-app.auth.enabled}")
    public Boolean authEnabled;

    @When("I call the get txn API with expected status of {int}")
    public void callTxnReqApi(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        if(authEnabled)
            requestSpec.header("Authorization", "Bearer " + BaseStepDef.accessToken);

        BaseStepDef.response = RestAssured.given(requestSpec)
                .baseUri(operationsAppConfig.operationAppContactPoint)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .get(operationsAppConfig.transactionRequestsEndpoint)
                .andReturn().asString();

        logger.info("GetTxn Request Response: " + BaseStepDef.response);
    }

    @And("I should have clientCorrelationId in response")
    public void checkClientCorrelationId() {
        assertThat(BaseStepDef.response).containsMatch("clientCorrelationId");
    }


}