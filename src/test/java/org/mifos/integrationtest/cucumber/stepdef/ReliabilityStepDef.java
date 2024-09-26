package org.mifos.integrationtest.cucumber.stepdef;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.GsmaP2PResponseDto;
import org.mifos.integrationtest.config.OperationsAppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

public class ReliabilityStepDef extends BaseStepDef {

    public List<String> transactionIds = new ArrayList<>();
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    OperationsAppConfig operationsAppConfig;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Given("I create a clientCorrelationId and tenant {string}")
    public void iCreateANewClientCorrelationId(String tenant) {
        BaseStepDef.clientCorrelationId = UUID.randomUUID().toString();
        BaseStepDef.tenant = tenant;
        assertThat(BaseStepDef.clientCorrelationId).isNotNull();
        assertThat(BaseStepDef.tenant).isNotNull();
    }

    @When("I call transfer api for {int} times and store transaction Ids")
    public void sendInboundTransfer(int count) throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException, InterruptedException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        requestSpec.header(Utils.X_CORRELATIONID, BaseStepDef.clientCorrelationId);
        while (count > 0) {
            BaseStepDef.response = RestAssured.given(requestSpec).baseUri(channelConnectorConfig.channelConnectorContactPoint)
                    .body(BaseStepDef.inboundTransferMockReq).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                    .when().post(channelConnectorConfig.transferEndpoint).andReturn().asString();

            logger.info("Transfer Request Response: {}", BaseStepDef.response);
            // Storing txn id in array
            GsmaP2PResponseDto gsmaP2PResponseDto = objectMapper.readValue(BaseStepDef.response, GsmaP2PResponseDto.class);
            transactionIds.add(gsmaP2PResponseDto.getTransactionId());
            Thread.sleep(2000);
            count--;
        }

    }

    @When("I call operations-app api with expected status {int} and match it with stored transaction Ids")
    public void checkFinalCount(int status) throws InterruptedException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        requestSpec.queryParam("size", "1");
        requestSpec.header("page", "0");
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + BaseStepDef.accessToken);
        }
        for (String transactionId : transactionIds) {
            requestSpec.queryParam("transactionId", transactionId);
            BaseStepDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(status).build())
                    .when().get(operationsAppConfig.transfersEndpoint)
                    .andReturn().asString();

            logger.info("GetTxn Request Response: " + BaseStepDef.response);
            assertThat(BaseStepDef.response).isNotEmpty();
        }
        // Removing all txn ids at the end as arraylist is fail-fast
        transactionIds.removeAll(transactionIds);
    }
}
