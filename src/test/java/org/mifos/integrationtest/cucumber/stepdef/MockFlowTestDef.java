package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.config.OperationsAppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class MockFlowTestDef extends BaseStepDef {

    @Value("${operations-app.auth.enabled}")
    public Boolean authEnabled;

    @Autowired
    OperationsAppConfig operationsAppConfig;



    @When("I call the outbound transfer endpoint with expected status {int}")
    public void iCallTheOutboundTransferEndpointWithExpectedStatus(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);

        BaseStepDef.response = RestAssured.given(requestSpec)
                .baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(BaseStepDef.inboundTransferMockReq)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .post(channelConnectorConfig.transferEndpoint)
                .andReturn().asString();

        logger.info("Inbound transfer Response: {}", BaseStepDef.response);
    }

    @When("I call the get txn API with expected status of {int} and clientCorrelationId")
    public void iCallTheGetTxnAPIWithExpectedStatusOfAndClientCorrelationId(int expectedStatus) {
            RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
            requestSpec.queryParam(Utils.X_CORRELATIONID,BaseStepDef.clientCorrelationId );
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
}
