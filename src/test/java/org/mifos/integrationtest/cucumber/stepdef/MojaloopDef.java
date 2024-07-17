package org.mifos.integrationtest.cucumber.stepdef;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.UUID;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.mojaloop.AddParticipantRequestBody;
import org.mifos.integrationtest.common.dto.mojaloop.AddUserAlsRequest;
import org.mifos.integrationtest.common.dto.mojaloop.Amount;
import org.mifos.integrationtest.common.dto.mojaloop.CallbackRequestBody;
import org.mifos.integrationtest.common.dto.mojaloop.Endpoint;
import org.mifos.integrationtest.common.dto.mojaloop.HubAccountSetupRequestBody;
import org.mifos.integrationtest.common.dto.mojaloop.InitialPositionAndLimitRequestBody;
import org.mifos.integrationtest.common.dto.mojaloop.Limit;
import org.mifos.integrationtest.common.dto.mojaloop.OracleOnboardRequestBody;
import org.mifos.integrationtest.common.dto.mojaloop.RecordFundsRequestBody;
import org.mifos.integrationtest.common.dto.mojaloop.SettlementModelRequestBody;
import org.mifos.integrationtest.config.MojaloopCallbackEndpoints;
import org.mifos.integrationtest.config.MojaloopConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MojaloopDef {

    @Autowired
    MojaloopConfig mojaloopConfig;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MojaloopCallbackEndpoints callbackEndpoints;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String CURRENCY = "TZS";

    protected String setBodyAddAlsUser(String fspId) throws JsonProcessingException {
        AddUserAlsRequest addUserAlsRequest = new AddUserAlsRequest();
        addUserAlsRequest.setCurrency(CURRENCY);
        addUserAlsRequest.setFspId(fspId);
        return objectMapper.writeValueAsString(addUserAlsRequest);
    }

    protected Boolean isHubAccountTypesAdded() {

        RequestSpecification requestSpec = Utils.getDefaultSpec();
        String endpoint = mojaloopConfig.mojaloopHubAccount;
        String response = RestAssured.given(requestSpec).baseUri(mojaloopConfig.mojaloopCentralLedgerBaseurl).when().expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().get(endpoint).andReturn().asString();

        int count = JsonParser.parseString(response).getAsJsonArray().size();
        logger.info(String.valueOf(count));
        return count >= 2;
    }

    protected void hubMultilateralSettlement() throws JsonProcessingException {

        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("Content-Type", "application/json");
        String endpoint = mojaloopConfig.mojaloopHubAccount;
        String requestBody = objectMapper.writeValueAsString(new HubAccountSetupRequestBody("HUB_MULTILATERAL_SETTLEMENT", CURRENCY));
        Response response = RestAssured.given(requestSpec).baseUri(mojaloopConfig.mojaloopCentralLedgerBaseurl).body(requestBody).when()
                .post(endpoint);

        validateResponse(response, "3003");
    }

    protected void hubReconciliation() throws JsonProcessingException {

        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("Content-Type", "application/json");
        String endpoint = mojaloopConfig.mojaloopHubAccount;
        String requestBody = objectMapper.writeValueAsString(new HubAccountSetupRequestBody("HUB_RECONCILIATION", CURRENCY));
        Response response = RestAssured.given(requestSpec).baseUri(mojaloopConfig.mojaloopCentralLedgerBaseurl).body(requestBody).when()
                .post(endpoint);

        validateResponse(response, "3003");
    }

    protected Boolean isSettlementModelsCreated() {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        String endpoint = mojaloopConfig.settlementModel;
        String response = RestAssured.given(requestSpec).baseUri(mojaloopConfig.mojaloopCentralLedgerBaseurl).when().expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().get(endpoint).andReturn().asString();

        int count = JsonParser.parseString(response).getAsJsonArray().size();
        logger.info(String.valueOf(count));
        return count >= 2;
    }

    protected void createSettlementModelDeferredNet() throws JsonProcessingException {

        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("Content-Type", "application/json");
        String endpoint = mojaloopConfig.settlementModel;
        String requestBody = objectMapper.writeValueAsString(settlementModelRequestBody("DEFERREDNET"));

        Response response = RestAssured.given(requestSpec).baseUri(mojaloopConfig.mojaloopCentralLedgerBaseurl).body(requestBody).when()
                .post(endpoint);

        validateResponse(response, "3000");
    }

    protected void createSettlementModelDeferredNetUSD() {

        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("Content-Type", "application/json");
        String endpoint = mojaloopConfig.settlementModel;
        SettlementModelRequestBody requestBody = settlementModelRequestBody("DEFERREDNETTZS");
        requestBody.setCurrency(CURRENCY);

        Response response = RestAssured.given(requestSpec).baseUri(mojaloopConfig.mojaloopCentralLedgerBaseurl).body(requestBody).when()
                .post(endpoint);

        validateResponse(response, "3000");
    }

    protected SettlementModelRequestBody settlementModelRequestBody(String name) {
        SettlementModelRequestBody requestBody = SettlementModelRequestBody.builder().name(name).settlementGranularity("NET")
                .settlementInterchange("MULTILATERAL").settlementDelay("DEFERRED").requireLiquidityCheck(true).ledgerAccountType("POSITION")
                .autoPositionReset(true).settlementAccountType("SETTLEMENT").build();
        return requestBody;
    }

    protected void addFsp(String fsp) throws JsonProcessingException {

        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("Content-Type", "application/json");
        String endpoint = mojaloopConfig.participant;
        String requestBody = objectMapper.writeValueAsString(new AddParticipantRequestBody(fsp, CURRENCY));

        Response response = RestAssured.given(requestSpec).baseUri(mojaloopConfig.mojaloopCentralLedgerBaseurl).body(requestBody).when()
                .post(endpoint);

        validateResponse(response, "3000");
    }

    protected void addInitialPositionAndLimit(String fsp) throws JsonProcessingException {

        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("Content-Type", "application/json");
        String endpoint = mojaloopConfig.initialPositionAndLimitEndpoint.replaceAll("\\{\\{fsp\\}\\}", fsp);

        Response response = RestAssured.given(requestSpec).baseUri(mojaloopConfig.mojaloopCentralLedgerBaseurl)
                .body(getInitialPositionAndLimitRequestBody()).when().post(endpoint);

        validateResponse(response, "2001");
    }

    public String getInitialPositionAndLimitRequestBody() throws JsonProcessingException {
        Limit limit = new Limit("NET_DEBIT_CAP", 1000000L);
        InitialPositionAndLimitRequestBody requestBody = new InitialPositionAndLimitRequestBody(CURRENCY, limit, 0L);
        return objectMapper.writeValueAsString(requestBody);
    }

    protected void addCallbackEndpoint(String client, String type, String value) throws JsonProcessingException {

        value = value.replaceAll("\\{\\{fsp\\}\\}", client);
        CallbackRequestBody requestBody = new CallbackRequestBody(type, value);
        logger.info(objectMapper.writeValueAsString(requestBody));
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("Content-Type", "application/json");
        String endpoint = mojaloopConfig.addCallbackEndpoint.replaceAll("\\{\\{fsp\\}\\}", client);

        Response responseBody = RestAssured.given(requestSpec).baseUri(mojaloopConfig.mojaloopCentralLedgerBaseurl)
                .body(objectMapper.writeValueAsString(requestBody)).expect().spec(new ResponseSpecBuilder().expectStatusCode(201).build())
                .when().post(endpoint).andReturn();

    }

    protected Boolean getCallbackEndpoints(String client) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        String endpoint = mojaloopConfig.addCallbackEndpoint.replaceAll("\\{\\{fsp\\}\\}", client);

        String responseBody = RestAssured.given(requestSpec).baseUri(mojaloopConfig.mojaloopCentralLedgerBaseurl).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().get(endpoint).andReturn().asString();

        int count = JsonParser.parseString(responseBody).getAsJsonArray().size();
        logger.info(String.valueOf(count));
        int callbackEndpointsRegistered = callbackEndpoints.getCallbackEndpoints().size();
        return count >= callbackEndpointsRegistered;
    }

    protected void setCallbackEndpoints() {

        String payerFsp = mojaloopConfig.payerFspId;
        String payeeFsp = mojaloopConfig.payeeFspId;
        String payeeFsp2 = mojaloopConfig.payeeFspId2;
        String payeeFsp3 = mojaloopConfig.payeeFspId3;
        callbackEndpoints.getCallbackEndpoints().forEach(callback -> {
            String value = callback.getValue().replaceAll("\\{\\{CALLBACK_HOST\\}\\}", "http://" + mojaloopConfig.mlConnectorHost);
            try {
                addCallbackEndpoint(payerFsp, callback.getType(), value);
                addCallbackEndpoint(payeeFsp, callback.getType(), value);
                addCallbackEndpoint(payeeFsp2, callback.getType(), value);
                addCallbackEndpoint(payeeFsp3, callback.getType(), value);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected void recordFunds(String fsp) throws JsonProcessingException {

        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("Content-Type", "application/json");
        String endpoint = mojaloopConfig.recordFundsEndpoint.replaceAll("\\{\\{fsp\\}\\}", fsp)
                .replaceAll("\\{\\{payerfspSettlementAccountId\\}\\}", "16");
        String requestBody = objectMapper.writeValueAsString(getRecordFundsRequestBody());

        Response responseBody = RestAssured.given(requestSpec).baseUri(mojaloopConfig.mojaloopCentralLedgerBaseurl).body(requestBody)
                .expect().spec(new ResponseSpecBuilder().expectStatusCode(202).build()).when().post(endpoint).andReturn();
    }

    private RecordFundsRequestBody getRecordFundsRequestBody() {
        Amount amount = new Amount(5000L, CURRENCY);
        return RecordFundsRequestBody.builder().transferId(UUID.randomUUID().toString()).externalReference("string").action("recordFundsIn")
                .reason("string").amount(amount).build();
    }

    protected Boolean oracleExists() {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("Content-Type", "application/json");
        requestSpec.header("Date", "");
        String endpoint = mojaloopConfig.oracleEndpoint;
        Response response = RestAssured.given(requestSpec).baseUri(mojaloopConfig.mojaloopAccountLookupAdminBaseurl).when().expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().get(endpoint);
        int count = JsonParser.parseString(response.getBody().asString()).getAsJsonArray().size();
        logger.info(String.valueOf(count));
        return count >= 1;
    }

    protected void oracleOnboard() throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("Content-Type", "application/json");
        requestSpec.header("Date", "");
        String endpoint = mojaloopConfig.oracleEndpoint;
        String requestBody = objectMapper.writeValueAsString(getOracleOnboardRequestBody());

        Response response = RestAssured.given(requestSpec).baseUri(mojaloopConfig.mojaloopAccountLookupAdminBaseurl).body(requestBody)
                .when().post(endpoint);

        validateResponse(response, "2001");
    }

    private OracleOnboardRequestBody getOracleOnboardRequestBody() {
        Endpoint endpoint = new Endpoint("http://moja-simulator/oracle", "URL");
        return OracleOnboardRequestBody.builder().oracleIdType("MSISDN").endpoint(endpoint).currency(CURRENCY).isDefault(true).build();
    }

    private void validateResponse(Response response, String acceptedErrorCode) {

        if (response.getStatusCode() == 400 || response.getStatusCode() == 500) {
            JsonObject jsonObject = JsonParser.parseString(response.getBody().asString()).getAsJsonObject();
            String errorCode = jsonObject.getAsJsonObject().get("errorInformation").getAsJsonObject().get("errorCode").getAsString();
            String errorDesc = jsonObject.getAsJsonObject().get("errorInformation").getAsJsonObject().get("errorDescription").getAsString();
            logger.info(errorCode);
            logger.info(errorDesc);
            if (!errorCode.equals(acceptedErrorCode) || !errorDesc.contains("already")) {

                throw new RuntimeException();
            }
            // todo - validate message as well
        } else if (!String.valueOf(response.getStatusCode()).matches("2\\d{2}")) {
            throw new RuntimeException();
        }
    }
}
