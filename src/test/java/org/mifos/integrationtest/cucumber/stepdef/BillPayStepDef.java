package org.mifos.integrationtest.cucumber.stepdef;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.connector.common.channel.dto.TransactionChannelRequestDTO;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.Bill;
import org.mifos.integrationtest.common.dto.BillDetails;
import org.mifos.integrationtest.common.dto.BillRTPReqDTO;
import org.mifos.integrationtest.common.dto.PayerFSPDetail;
import org.mifos.integrationtest.common.dto.billPayP2G.BillPaymentsReqDTO;
import org.mifos.integrationtest.config.BillPayConnectorConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.google.common.truth.Truth.assertThat;

public class BillPayStepDef extends BaseStepDef {

    @Autowired
    BillPaymentsReqDTO billPaymentsReqDTO;

    @Autowired
    private BillPayConnectorConfig billPayConnectorConfig;
    private static String billerId;
    private static BillRTPReqDTO billRTPReqDTO;
    private static String billId  = "12345";

    @Then("I can create DTO for Biller RTP Request")
    public void iCanCreateDTOForBillerRTPRequest() {
        Bill bill =new Bill("Test", 100.0);
        PayerFSPDetail payerFSPDetail = new PayerFSPDetail("lion", "1223455");
        billRTPReqDTO = new BillRTPReqDTO("123445", billId, "00", payerFSPDetail, bill);



    }

    @And("I have bill id as {string}")
    public void iHaveBillIdAs(String billId) {
        BaseStepDef.billId = billId;
        assertThat(BaseStepDef.billId).isNotEmpty();
    }



    @When("I call the get bills api with billid with expected status of {int} and callbackurl as {string}")
    public void iCallTheGetBillsApiWithBillidWithExpectedStatusOf(int expectedStatus,String callbackUrl) throws JsonProcessingException, JSONException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);

        requestSpec.header("X-CorrelationID", BaseStepDef.clientCorrelationId.toString());
        requestSpec.header("X-CallbackURL",billPayConnectorConfig.callbackURL + callbackUrl);
        requestSpec.header("X-PayerFSP-Id", "lion");
        requestSpec.queryParam("fields","inquiry");
        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(billPayConnectorConfig.billPayContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(billPayConnectorConfig.inquiryEndpoint.replace("{billId}",billId)).andReturn().asString();

        logger.info("Bill Pay response: {}", BaseStepDef.response);
        JSONObject jsonObject = new JSONObject(BaseStepDef.response);
        BaseStepDef.transactionId = jsonObject.getString("transactionId");
       assertThat(BaseStepDef.transactionId.equals ("NA")).isFalse();

    }
    @And("I should get transactionId in response")
    public void iShouldGetBatchIdInResponse() throws JSONException {
        JSONObject jsonObject = new JSONObject(BaseStepDef.response);
        BaseStepDef.transactionId = jsonObject.getString("transactionId");

    }

    @And("I should have startedAt and completedAt and workflowInstanceKey in response and not null")
    public void iShouldHaveStartedAtAndCompletedAtAndWorkflowInstanceKeyInResponse() throws JSONException {
        assertThat(BaseStepDef.response).containsMatch("startedAt");
        assertThat(BaseStepDef.response).containsMatch("completedAt");
        assertThat(BaseStepDef.response).containsMatch("workflowInstanceKey");

        JSONObject jsonObject = new JSONObject(BaseStepDef.response);
        JSONArray jsonArray = (JSONArray) jsonObject.get("content");
        JSONObject content = (JSONObject) jsonArray.get(0);
        String value = content.get("startedAt").toString();
        assertThat(value).isNotNull();
        value = content.get("completedAt").toString();
        assertThat(value).isNotNull();
        value = content.get("workflowInstanceKey").toString();
        assertThat(value).isNotNull();

    }

    @And("I can mock payment notification request")
    public void iCanMockPaymentNotificationRequest() throws JsonProcessingException {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{")
                .append("\"clientCorrelationId\": " + "\"" + BaseStepDef.clientCorrelationId + "\"" + ",")
                .append("\"billInquiryRequestId\": \"27710101999\",")
                .append("\"billInquiryRequestId\": " + "\"" + UUID.randomUUID() + "\"" + ",")
                .append("\"billId\": " + "\"" + BaseStepDef.billId + "\"" + ",")
                .append("\"paymentReferenceID\": " + "\"" + UUID.randomUUID() + "\"")
                .append("}");
        String json = jsonBuilder.toString();
        billPaymentsReqDTO = objectMapper.readValue(json, BillPaymentsReqDTO.class);
        BaseStepDef.inboundTransferReqP2G = billPaymentsReqDTO;
        logger.info("inboundTransferReqP2G: {}", BaseStepDef.inboundTransferReqP2G);
        assertThat(BaseStepDef.inboundTransferReqP2G).isNotNull();



    }

    @When("I call the payment notification api expected status of {int} and callbackurl as {string}")
    public void iCallThePaymentNotificationApiExpectedStatusOf(int expectedStatus,String callbackurl) throws JSONException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        requestSpec.header("X-Platform-TenantId", BaseStepDef.tenant);
        requestSpec.header("X-CorrelationID", BaseStepDef.clientCorrelationId);
        requestSpec.header("X-PayerFSP-Id", "lion");
        requestSpec.queryParam("fields","inquiry");
        requestSpec.header("X-CallbackURL",billPayConnectorConfig.callbackURL + callbackurl);
        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(billPayConnectorConfig.billPayContactPoint)
                .body(BaseStepDef.inboundTransferReqP2G)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus)
                        .build()).when()
                .post(billPayConnectorConfig.paymentsEndpoint).andReturn().asString();

        logger.info("Payment notiifcation response: {}", BaseStepDef.response);
        JSONObject jsonObject = new JSONObject(BaseStepDef.response);
        BaseStepDef.transactionId = jsonObject.getString("transactionId");
        assertThat(BaseStepDef.transactionId.equals ("NA")).isFalse();
    }

    @When("I call the mock get bills api from PBB to Biller with billid with expected status of {int}")
    public void iCallTheMockGetBillsApiPBBToBillerAggWithBillidWithExpectedStatusOf(int expectedStatus) {
            RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
            requestSpec.header("X-Platform-TenantId", BaseStepDef.tenant);
            requestSpec.header("X-CorrelationID", BaseStepDef.clientCorrelationId);
            requestSpec.header("X-PayerFSP-Id", "lion");
            BaseStepDef.response = RestAssured.given(requestSpec).baseUri(billPayConnectorConfig.billPayContactPoint)
                    .expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus)
                            .build()).when()
                    .get(billPayConnectorConfig.inquiryEndpoint.replace("{billId}",billId)).andReturn().asString();

            logger.info("Txn Req response: {}", BaseStepDef.response);

    }

    @When("I call the mock bills payment api from PBB to Biller with billid with expected status of {int}")
    public void iCallTheMockBillsPaymentApiFromPBBToBillerWithBillidWithExpectedStatusOf(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        requestSpec.header("X-Platform-TenantId", BaseStepDef.tenant);
        requestSpec.header("X-CorrelationID", BaseStepDef.clientCorrelationId);
        requestSpec.header("X-PayerFSP-Id", "lion");
        requestSpec.header("X-CallbackURL", "https://webhook.site/b44174ab-04b4-4b0d-8426-a3c54bc2f794");
        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(billPayConnectorConfig.billPayContactPoint)
                .body(BaseStepDef.inboundTransferReqP2G)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus)
                        .build()).when()
                .post(billPayConnectorConfig.paymentsEndpoint).andReturn().asString();

        logger.info("Txn Req response: {}", BaseStepDef.response);
    }
    @Then("I should be able to verify that the {string} method to {string} endpoint received a request with code in body")
    public void iShouldBeAbleToVerifyThatTheMethodToEndpointReceivedRequestWithASpecificBody(String httpmethod, String endpoint) {
        verify(putRequestedFor(urlEqualTo(endpoint)).withRequestBody(matchingJsonPath("$.code")));
    }

    @Then("I should be able to extract response body from callback for bill pay")
    public void iShouldBeAbleToExtractResponseBodyFromCallbackForBillPay() {
        boolean flag = false;
        List<ServeEvent> allServeEvents = getAllServeEvents();
        for (int i = allServeEvents.size()-1; i >= 0; i--) {
            ServeEvent request = allServeEvents.get(i);
            if (!(request.getRequest().getBodyAsString()).isEmpty()) {
                JsonNode rootNode = null;
                flag = true;
                try {
                    rootNode = objectMapper.readTree(request.getRequest().getBody());
                    logger.info("Rootnode value:" + rootNode);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if(rootNode != null && rootNode.has("billId") && rootNode.get("billId").asText().equals("001")) {
                    String requestId = null;
                    if (rootNode.has("requestId")) {
                        requestId = rootNode.get("requestId").asText();
                    }
                    assertThat(requestId).isNotEmpty();
                    String rtpStatus = null;
                    if (rootNode.has("code")) {
                        rtpStatus = rootNode.get("code").asText();
                    }
                    assertThat(rtpStatus).isNotEmpty();
                    String billId = null;
                    if (rootNode.has("billId")) {
                        billId = rootNode.get("billId").asText();
                    }
                    assertThat(billId).isNotEmpty();
                }
            }

        }
        assertThat(flag).isTrue();
    }
    @Then("I should be able to extract response body from callback for bill notification")
    public void iShouldBeAbleToExtractResponseBodyFromCallbackForBillNotification() {
        boolean flag = false;
        List<ServeEvent> allServeEvents = getAllServeEvents();
        for (int i = allServeEvents.size()-1; i >= 0; i--) {
            ServeEvent request = allServeEvents.get(i);
            if (!(request.getRequest().getBodyAsString()).isEmpty()) {
                JsonNode rootNode = null;
                flag = true;
                try {
                    rootNode = objectMapper.readTree(request.getRequest().getBody());
                    logger.info("Rootnode value:" + rootNode);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if(rootNode != null && rootNode.has("billId") && rootNode.get("billId").asText().equals("001")) {
                    String requestId = null;
                    if (rootNode.has("billRequestId")) {
                        requestId = rootNode.get("billRequestId").asText();
                    }
                    assertThat("billRequestId").isNotEmpty();
                    String rtpStatus = null;
                    if (rootNode.has("code")) {
                        rtpStatus = rootNode.get("code").asText();
                    }
                    assertThat(rtpStatus).isNotEmpty();
                    String billId = null;
                    if (rootNode.has("billId")) {
                        billId = rootNode.get("billId").asText();
                    }
                    assertThat(billId).isNotEmpty();
                }
            }

        }
        assertThat(flag).isTrue();
    }


    @And("I can call the biller RTP request API with expected status of {int} and {string} endpoint")
    public void iCanCallTheBillerRTPRequestAPIWithExpectedStatusOfAndEndpoint(int expectedStatus, String stub) throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(billRTPReqDTO);
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        BaseStepDef.response = RestAssured.given(requestSpec)
                .header("Content-Type", "application/json")
                .header("X-Callback-URL",billPayConnectorConfig.callbackURL+ stub)
                .header("X-Biller-Id", billerId)
                .header("X-Client-Correlation-ID",clientCorrelationId)
                .header("X-Platform-TenantId",tenant)
                .baseUri(billPayConnectorConfig.billPayContactPoint)
                .body(billRTPReqDTO)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .post(billPayConnectorConfig.billerRtpEndpoint)
                .andReturn().asString();


        logger.info("RTP Response: {}", BaseStepDef.response);
    }

    @And("I have a billerId as {string}")
    public void iHaveABillerIdAs(String biller) {
        billerId =  biller;
    }
    @And("I can extract the callback body and assert the rtpStatus")
    public void iCanExtractTheCallbackBodyAndAssertTheRtpStatus() {
        boolean flag = false;
        List<ServeEvent> allServeEvents = getAllServeEvents();
        for (int i = allServeEvents.size()-1; i >= 0; i--) {
            ServeEvent request = allServeEvents.get(i);
            if (!(request.getRequest().getBodyAsString()).isEmpty()) {
                JsonNode rootNode = null;
                flag = true;
                try {
                    rootNode = objectMapper.readTree(request.getRequest().getBody());
                    logger.info("Rootnode value:" + rootNode);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if(rootNode != null && rootNode.has("billId") && rootNode.get("billId").asText().equals(billId)) {
                    String requestId = null;
                    if (rootNode.has("requestId")) {
                        requestId = rootNode.get("requestId").asText();
                    }
                    assertThat(requestId).isNotEmpty();
                    String rtpStatus = null;
                    if (rootNode.has("rtpStatus")) {
                        rtpStatus = rootNode.get("rtpStatus").asText();
                    }
                    assertThat(rtpStatus).isNotEmpty();
                    String rtpId = null;
                    if (rootNode.has("rtpId")) {
                        rtpId = rootNode.get("rtpId").asText();
                    }
                    assertThat(rtpId).isNotEmpty();
                    String billId = null;
                    if (rootNode.has("billId")) {
                        billId = rootNode.get("billId").asText();
                    }
                    assertThat(billId).isNotEmpty();
                }
            }

        }
        assertThat(flag).isTrue();
    }
}
