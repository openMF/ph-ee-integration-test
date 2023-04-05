package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.common.Utils;
import io.restassured.builder.ResponseSpecBuilder;

public class AccountStepDef extends BaseStepDef{
    @When("I call accountStatus endpoint for {string} MSISDN, with status of {int}")
    public void iCallAccountBalanceEndpointWithStatusOf(String msisdn,int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        BaseStepDef.msisdn = msisdn;
        logger.info("url: {}",amsConnectorConfig.accountStatusEndpoint);
        BaseStepDef.response = RestAssured.given(requestSpec)
                .baseUri(amsConnectorConfig.amsConnectorContactPoint)
                .pathParam("identifierType","MSISDN")
                .pathParam("identifierId",BaseStepDef.msisdn)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .get(amsConnectorConfig.accountStatusEndpoint)
                .andReturn().asString();

        logger.info("Account status Response: {}", BaseStepDef.response);
        logger.info("Account ID {}", BaseStepDef.msisdn);


    }
}
