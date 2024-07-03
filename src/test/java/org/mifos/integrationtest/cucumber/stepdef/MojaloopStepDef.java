package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;
import static io.restassured.config.EncoderConfig.encoderConfig;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.config.MojaloopConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class MojaloopStepDef extends BaseStepDef {

    @Autowired
    MojaloopConfig mojaloopConfig;

    @Autowired
    MojaloopDef mojaloopDef;

    @Autowired
    ScenarioScopeState scenarioScopeState;

    @Then("I add {string} to als")
    public void addUsersToALS(String client) throws JsonProcessingException {

        String clientIdentifierId;
        String fspId;
        switch (client) {
            case "payer" -> {
                clientIdentifierId = scenarioScopeState.payerIdentifier;
                fspId = mojaloopConfig.payerFspId;
            }
            case "payee2" -> {
                clientIdentifierId = scenarioScopeState.payeeIdentifier;
                fspId = mojaloopConfig.payeeFspId2;
            }
            case "payee3" -> {
                clientIdentifierId = scenarioScopeState.payeeIdentifier;
                fspId = mojaloopConfig.payeeFspId3;
            }
            default -> {
                clientIdentifierId = scenarioScopeState.payeeIdentifier;
                fspId = mojaloopConfig.payeeFspId;
            }
        }

        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("FSPIOP-Source", fspId);
        requestSpec.header("Date", getCurrentDateInFormat());
        requestSpec.header("Accept", "application/vnd.interoperability.participants+json;version=1");
        // requestSpec.header("Content-Type", "application/vnd.interoperability.participants+json;version=1.0");

        String endpoint = mojaloopConfig.addUserToAlsEndpoint;
        endpoint = endpoint.replaceAll("\\{\\{identifierType\\}\\}", "MSISDN");
        endpoint = endpoint.replaceAll("\\{\\{identifier\\}\\}", clientIdentifierId);

        String requestBody = mojaloopDef.setBodyAddAlsUser(fspId);

        logger.info(mojaloopConfig.mojaloopBaseurl);
        logger.info(requestBody);
        logger.info(endpoint);

        String response = RestAssured.given(requestSpec).baseUri(mojaloopConfig.mojaloopBaseurl)
                .config(RestAssured.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .body(requestBody).contentType("application/vnd.interoperability.participants+json;version=1.0").expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(202).build()).when().post(endpoint).andReturn().asString();

        logger.info(response);
        assertThat(response).isNotNull();
    }

    @Then("I add {string} with account id {string} to als")
    public void addBudgetAccountToALS(String client, String accountId) throws JsonProcessingException {

        String clientIdentifierId;
        String fspId;
        if (client.equals("payer")) {
            clientIdentifierId = scenarioScopeState.payerIdentifier;
            fspId = mojaloopConfig.payerFspId;
        } else {
            clientIdentifierId = scenarioScopeState.payeeIdentifier;
            fspId = mojaloopConfig.payeeFspId;
        }

        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("FSPIOP-Source", fspId);
        requestSpec.header("Date", getCurrentDateInFormat());
        requestSpec.header("Accept", "application/vnd.interoperability.participants+json;version=1");
        // requestSpec.header("Content-Type", "application/vnd.interoperability.participants+json;version=1.0");

        String endpoint = mojaloopConfig.addUserToAlsEndpoint;
        endpoint = endpoint.replaceAll("\\{\\{identifierType\\}\\}", "MSISDN");
        endpoint = endpoint.replaceAll("\\{\\{identifier\\}\\}", accountId);

        String requestBody = mojaloopDef.setBodyAddAlsUser(fspId);

        logger.info(mojaloopConfig.mojaloopBaseurl);
        logger.info(requestBody);
        logger.info(endpoint);

        String response = RestAssured.given(requestSpec).baseUri(mojaloopConfig.mojaloopBaseurl)
                .config(RestAssured.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .body(requestBody).contentType("application/vnd.interoperability.participants+json;version=1.0").expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(202).build()).when().post(endpoint).andReturn().asString();

        logger.info(response);
        assertThat(response).isNotNull();
    }


    @Given("I am setting up Mojaloop")
    public void mojaloopSetup() throws JsonProcessingException {

        String payerFsp = mojaloopConfig.payerFspId;
        String payeeFsp = mojaloopConfig.payeeFspId;
        String payeeFsp2 = mojaloopConfig.payeeFspId2;
        String payeeFsp3 = mojaloopConfig.payeeFspId3;


        if (!mojaloopDef.isHubAccountTypesAdded()) {

            logger.info("Calling hub account apis");

            mojaloopDef.hubMultilateralSettlement();
            mojaloopDef.hubReconciliation();
        }

        if (!mojaloopDef.isSettlementModelsCreated()) {

            logger.info("Calling Settlement Models apis");

            mojaloopDef.createSettlementModelDeferredNet();
            mojaloopDef.createSettlementModelDeferredNetUSD();
        }

        mojaloopDef.addFsp(payerFsp);
        mojaloopDef.addFsp(payeeFsp);
        mojaloopDef.addFsp(payeeFsp2);
        mojaloopDef.addFsp(payeeFsp3);
        mojaloopDef.addInitialPositionAndLimit(payerFsp);
        mojaloopDef.addInitialPositionAndLimit(payeeFsp);
        mojaloopDef.addInitialPositionAndLimit(payeeFsp2);
        mojaloopDef.addInitialPositionAndLimit(payeeFsp3);

        if (!mojaloopDef.getCallbackEndpoints(payerFsp) || !mojaloopDef.getCallbackEndpoints(payeeFsp) || !mojaloopDef.getCallbackEndpoints(payeeFsp2)
         || !mojaloopDef.getCallbackEndpoints(payeeFsp3)) {
            mojaloopDef.setCallbackEndpoints();
        }

        mojaloopDef.recordFunds(payerFsp);

        if (!mojaloopDef.oracleExists()) {
            mojaloopDef.oracleOnboard();
        }
    }

}
