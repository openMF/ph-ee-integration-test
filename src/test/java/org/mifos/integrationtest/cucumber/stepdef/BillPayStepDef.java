package org.mifos.integrationtest.cucumber.stepdef;

import static com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.connector.common.channel.dto.PhErrorDTO;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.Alias;
import org.mifos.integrationtest.common.dto.Bill;
import org.mifos.integrationtest.common.dto.BillRTPReqDTO;
import org.mifos.integrationtest.common.dto.PayerFSPDetail;
import org.mifos.integrationtest.common.dto.billpayp2g.BillPaymentsReqDTO;
import org.mifos.integrationtest.config.BillPayConnectorConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class BillPayStepDef extends BaseStepDef {

    @Autowired
    BillPaymentsReqDTO billPaymentsReqDTO;

    @Autowired
    private BillPayConnectorConfig billPayConnectorConfig;
    private static String billerId;
    private static BillRTPReqDTO billRTPReqDTO;
    private static String billId = "12345";
    private static String rtpResponse;

    @Then("I can create DTO for Biller RTP Request")
    public void iCanCreateDTOForBillerRTPRequest() {
        Bill bill = new Bill("Test", 100.0);
        PayerFSPDetail payerFSPDetail = new PayerFSPDetail("lion", "1223455");
        billRTPReqDTO = new BillRTPReqDTO("123445", billId, "00", payerFSPDetail, bill);

    }

    @And("I have bill id as {string}")
    public void iHaveBillIdAs(String billId) {
        scenarioScopeState.billId = billId;
        assertThat(scenarioScopeState.billId).isNotEmpty();
    }

    @When("I call the get bills api with billid with expected status of {int} and callbackurl as {string}")
    public void iCallTheGetBillsApiWithBillidWithExpectedStatusOf(int expectedStatus, String callbackUrl)
            throws JsonProcessingException, JSONException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);

        requestSpec.header("X-CorrelationID", scenarioScopeState.clientCorrelationId.toString());
        requestSpec.header("X-CallbackURL", billPayConnectorConfig.callbackURL + callbackUrl);
        requestSpec.header("X-PayerFSP-Id", "lion");
        requestSpec.queryParam("fields", "inquiry");
        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(billPayConnectorConfig.billPayContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(billPayConnectorConfig.inquiryEndpoint.replace("{billId}", scenarioScopeState.billId)).andReturn().asString();

        logger.info("Bill Pay response: {}", scenarioScopeState.response);
        JSONObject jsonObject = new JSONObject(scenarioScopeState.response);
        scenarioScopeState.transactionId = jsonObject.getString("transactionId");
        assertThat(scenarioScopeState.transactionId.equals("NA")).isFalse();

    }

    @And("I should get transactionId in response")
    public void iShouldGetBatchIdInResponse() throws JSONException {
        JSONObject jsonObject = new JSONObject(scenarioScopeState.response);
        scenarioScopeState.transactionId = jsonObject.getString("transactionId");

    }

    @And("I should have startedAt and completedAt and workflowInstanceKey in response and not null")
    public void iShouldHaveStartedAtAndCompletedAtAndWorkflowInstanceKeyInResponse() throws JSONException {
        assertThat(scenarioScopeState.response).containsMatch("startedAt");
        assertThat(scenarioScopeState.response).containsMatch("completedAt");
        assertThat(scenarioScopeState.response).containsMatch("workflowInstanceKey");

        JSONObject jsonObject = new JSONObject(scenarioScopeState.response);
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
        BillPaymentsReqDTO billPaymentsReqDTO = new BillPaymentsReqDTO();
        billPaymentsReqDTO.setBillId(scenarioScopeState.billId);
        billPaymentsReqDTO.setPaymentReferenceID(UUID.randomUUID().toString());
        billPaymentsReqDTO.setClientCorrelationId(scenarioScopeState.clientCorrelationId);
        billPaymentsReqDTO.setBillInquiryRequestId(scenarioScopeState.clientCorrelationId);
        scenarioScopeState.inboundTransferReqP2G = billPaymentsReqDTO;
        logger.info("inboundTransferReqP2G: {}", scenarioScopeState.inboundTransferReqP2G);
        assertThat(scenarioScopeState.inboundTransferReqP2G).isNotNull();

    }

    @And("I can mock payment notification request with missing values")
    public void iCanMockPaymentNotificationRequestwithMissingValues() throws JsonProcessingException {
        BillPaymentsReqDTO billPaymentsReqDTO = new BillPaymentsReqDTO();
        billPaymentsReqDTO.setBillId(scenarioScopeState.billId);
        billPaymentsReqDTO.setPaymentReferenceID(UUID.randomUUID().toString());
        billPaymentsReqDTO.setClientCorrelationId(scenarioScopeState.clientCorrelationId);
        scenarioScopeState.inboundTransferReqP2G = billPaymentsReqDTO;
        logger.info("inboundTransferReqP2G: {}", scenarioScopeState.inboundTransferReqP2G);
        assertThat(scenarioScopeState.inboundTransferReqP2G).isNotNull();

    }

    @When("I call the payment notification api expected status of {int} and callbackurl as {string}")
    public void iCallThePaymentNotificationApiExpectedStatusOf(int expectedStatus, String callbackurl) throws JSONException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        requestSpec.header("X-Platform-TenantId", scenarioScopeState.tenant);
        requestSpec.header("X-CorrelationID", scenarioScopeState.clientCorrelationId);
        requestSpec.header("X-PayerFSP-Id", "lion");
        requestSpec.queryParam("fields", "inquiry");
        requestSpec.header("X-CallbackURL", billPayConnectorConfig.callbackURL + callbackurl);
        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(billPayConnectorConfig.billPayContactPoint)
                .body(scenarioScopeState.inboundTransferReqP2G).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .post(billPayConnectorConfig.paymentsEndpoint).andReturn().asString();

        logger.info("Payment notiifcation response: {}", scenarioScopeState.response);
        JSONObject jsonObject = new JSONObject(scenarioScopeState.response);
        scenarioScopeState.transactionId = jsonObject.getString("transactionId");
        assertThat(scenarioScopeState.transactionId.equals("NA")).isFalse();
    }

    @When("I call the mock get bills api from PBB to Biller with billid with expected status of {int}")
    public void iCallTheMockGetBillsApiPBBToBillerAggWithBillidWithExpectedStatusOf(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        requestSpec.header("X-Platform-TenantId", scenarioScopeState.tenant);
        requestSpec.header("X-CorrelationID", scenarioScopeState.clientCorrelationId);
        requestSpec.header("X-PayerFSP-Id", "lion");
        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(billPayConnectorConfig.billPayContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(billPayConnectorConfig.inquiryEndpoint.replace("{billId}", billId)).andReturn().asString();

        logger.info("Txn Req response: {}", scenarioScopeState.response);

    }

    @When("I call the mock bills payment api from PBB to Biller with billid with expected status of {int}")
    public void iCallTheMockBillsPaymentApiFromPBBToBillerWithBillidWithExpectedStatusOf(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        requestSpec.header("X-Platform-TenantId", scenarioScopeState.tenant);
        requestSpec.header("X-CorrelationID", scenarioScopeState.clientCorrelationId);
        requestSpec.header("X-PayerFSP-Id", "lion");
        requestSpec.header("X-CallbackURL", "https://webhook.site/b44174ab-04b4-4b0d-8426-a3c54bc2f794");
        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(billPayConnectorConfig.billPayContactPoint)
                .body(scenarioScopeState.inboundTransferReqP2G).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .post(billPayConnectorConfig.paymentsEndpoint).andReturn().asString();

        logger.info("Txn Req response: {}", scenarioScopeState.response);
    }

    @Then("I should be able to verify that the {string} method to {string} endpoint received a request with code in body")
    public void iShouldBeAbleToVerifyThatTheMethodToEndpointReceivedRequestWithASpecificBody(String httpmethod, String endpoint) {
        verify(putRequestedFor(urlEqualTo(endpoint)).withRequestBody(matchingJsonPath("$.code")));
    }

    @Then("I should be able to extract response body from callback for bill pay")
    public void iShouldBeAbleToExtractResponseBodyFromCallbackForBillPay() {
        await().atMost(awaitMost, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            boolean flag = false;
            List<ServeEvent> allServeEvents = getAllServeEvents();
            for (int i = allServeEvents.size() - 1; i >= 0; i--) {
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
                    if (rootNode != null && rootNode.has("billId") && rootNode.get("billId").asText().equals("001")) {
                        String reason = null;
                        if (rootNode.has("reason")) {
                            reason = rootNode.get("reason").asText();
                        }
                        assertThat(reason).isNotEmpty();
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
        });
    }

    @Then("I should be able to extract response body from callback for bill notification")
    public void iShouldBeAbleToExtractResponseBodyFromCallbackForBillNotification() {
        await().atMost(awaitMost, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            boolean flag = false;
            List<ServeEvent> allServeEvents = getAllServeEvents();
            for (int i = allServeEvents.size() - 1; i >= 0; i--) {
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
                    if (rootNode != null && rootNode.has("billId") && rootNode.get("billId").asText().equals("001")) {
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
        });
    }

    @And("I can call the biller RTP request API with expected status of {int} and {string} endpoint")
    public void iCanCallTheBillerRTPRequestAPIWithExpectedStatusOfAndEndpoint(int expectedStatus, String stub)
            throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(billRTPReqDTO);
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        scenarioScopeState.response = RestAssured.given(requestSpec).header("Content-Type", "application/json")
                .header("X-Callback-URL", billPayConnectorConfig.callbackURL + stub).header("X-Biller-Id", billerId)
                .header("X-Client-Correlation-ID", scenarioScopeState.clientCorrelationId)
                .header("X-Platform-TenantId", scenarioScopeState.tenant).baseUri(billPayConnectorConfig.billPayContactPoint)
                .body(billRTPReqDTO).expect().spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .post(billPayConnectorConfig.billerRtpEndpoint).andReturn().asString();

        rtpResponse = scenarioScopeState.response;
        logger.info("RTP Response: {}", scenarioScopeState.response);
    }

    @And("I have a billerId as {string}")
    public void iHaveABillerIdAs(String biller) {
        billerId = biller;
    }

    @And("I can extract the callback body and assert the rtpStatus")
    public void iCanExtractTheCallbackBodyAndAssertTheRtpStatus() {
        await().atMost(awaitMost, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            boolean flag = false;
            List<ServeEvent> allServeEvents = getAllServeEvents();
            for (int i = allServeEvents.size() - 1; i >= 0; i--) {
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
                    if (rootNode != null && rootNode.has("billId") && rootNode.get("billId").asText().equals(billId)) {
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
        });
    }

    @Then("I should be able to extract response body from callback for biller unidentified")
    public void iShouldBeAbleToExtractResponseBodyFromCallbackForBillerUnidentified() {
        await().atMost(awaitMost, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            boolean flag = false;
            List<ServeEvent> allServeEvents = getAllServeEvents();
            for (int i = allServeEvents.size() - 1; i >= 0; i--) {
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
                    if (rootNode != null && rootNode.has("responseCode") && rootNode.get("responseCode").asText().equals("01")) {
                        String responseDescription = null;
                        if (rootNode.has("responseDescription")) {
                            responseDescription = rootNode.get("responseDescription").asText();
                        }
                        assertThat(responseDescription).isNotEmpty();
                        assertThat(responseDescription).contains("Unindentified Biller");
                        String responseCode = null;
                        if (rootNode.has("responseCode")) {
                            responseCode = rootNode.get("responseCode").asText();
                        }
                        assertThat(responseCode).isNotEmpty();
                    }
                }

            }
            assertThat(flag).isTrue();
        });
    }

    @Then("I should be able to extract response body from callback for bill invalid")
    public void iShouldBeAbleToExtractResponseBodyFromCallbackForBillInvalid() {
        await().atMost(awaitMost, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            boolean flag = false;
            List<ServeEvent> allServeEvents = getAllServeEvents();
            for (int i = allServeEvents.size() - 1; i >= 0; i--) {
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
                    if (rootNode != null && rootNode.has("code") && rootNode.get("code").asText().equals("01")) {
                        String reason = null;
                        if (rootNode.has("reason")) {
                            reason = rootNode.get("reason").asText();
                        }
                        assertThat(reason).isNotEmpty();
                        assertThat(reason).contains("Invalid Bill ID");
                        String code = null;
                        if (rootNode.has("code")) {
                            code = rootNode.get("code").asText();
                        }
                        assertThat(code).isNotEmpty();
                    }
                }

            }
            assertThat(flag).isTrue();
        });
    }

    @And("I should get Payer FSP not found in response")
    public void iShouldGetDataInResponse() throws JSONException {
        JSONObject jsonObject = new JSONObject(scenarioScopeState.response);
        scenarioScopeState.transactionId = jsonObject.getString("transactionId");
        assertThat(scenarioScopeState.transactionId.equals("Participant Not Onboarded")).isTrue();
    }

    @Then("I should be able to extract response body from callback for empty bill id")
    public void iShouldBeAbleToExtractResponseBodyFromCallbackForEmptyBillId() {
        await().atMost(awaitMost, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            boolean flag = false;
            List<ServeEvent> allServeEvents = getAllServeEvents();
            for (int i = allServeEvents.size() - 1; i >= 0; i--) {
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
                    if (rootNode != null && rootNode.has("code") && rootNode.get("code").asText().equals("01")) {
                        if (rootNode.get("clientCorrelationId").asText().equals(scenarioScopeState.clientCorrelationId)) {
                            String reason = null;
                            if (rootNode.has("reason")) {
                                reason = rootNode.get("reason").asText();
                            }
                            assertThat(reason).isNotEmpty();
                            assertThat(reason).contains("Empty Bill ID");
                            String code = null;
                            if (rootNode.has("code")) {
                                code = rootNode.get("code").asText();
                            }
                            assertThat(code).isNotEmpty();
                        }
                    }

                }

            }
            assertThat(flag).isTrue();
        });
    }

    @Then("I should be able to extract response body from callback for bill notification with missing values")
    public void iShouldBeAbleToExtractResponseBodyFromCallbackForBillNotificationWithMissingValues() throws JSONException {
        await().atMost(awaitMost, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {

            JSONObject jsonObject = new JSONObject(scenarioScopeState.response);
            scenarioScopeState.transactionId = jsonObject.getString("transactionId");
            assertThat(scenarioScopeState.transactionId
                    .equals("Invalid Request: Mandatory Fields Missing, Missing field is billInquiryRequestId")).isTrue();
        });
    }

    @Then("I should be able to extract response body from callback for bill already paid")
    public void iShouldBeAbleToExtractResponseBodyFromCallbackForBillAlreadyPaid() {
        await().atMost(awaitMost, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            boolean flag = false;
            List<ServeEvent> allServeEvents = getAllServeEvents();
            for (int i = allServeEvents.size() - 1; i >= 0; i--) {
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
                    if (rootNode != null && rootNode.has("code") && rootNode.get("code").asText().equals("01")) {
                        if (rootNode.has("requestID")
                                && rootNode.get("requestID").asText().equals(scenarioScopeState.clientCorrelationId)) {
                            String reason = null;
                            if (rootNode.has("reason")) {
                                reason = rootNode.get("reason").asText();
                            }
                            assertThat(reason).isNotEmpty();
                            assertThat(reason).contains("Bill Payment Failed: Bill Already Paid");
                            String code = null;
                            if (rootNode.has("code")) {
                                code = rootNode.get("code").asText();
                            }
                            assertThat(code).isNotEmpty();
                        }
                    }
                }

            }
            assertThat(flag).isTrue();
        });
    }

    @Then("I should remove all server events")
    public void iShouldNotBeAbleToRemoveAllServerEvents() {
        boolean flag = false;
        WireMock.resetAllRequests();
        List<ServeEvent> allServeEvents = getAllServeEvents();
        assertThat(allServeEvents.size()).isEqualTo(0);
    }

    @Then("I should not get a response from callback for bill")
    public void iShouldNotBeAbleToExtractResponseBodyFromCallbackForBill() {
        await().atMost(awaitMost, SECONDS).pollDelay(pollDelay, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            boolean flag = false;
            List<ServeEvent> allServeEvents = getAllServeEvents();
            if (allServeEvents.isEmpty()) {
                flag = true;
            } else {
                for (int i = allServeEvents.size() - 1; i >= 0; i--) {
                    ServeEvent request = allServeEvents.get(i);
                    if (!(request.getRequest().getUrl().equals("/billNotificationsTimeout"))) {
                        flag = true;
                    }
                }
            }
            assertThat(flag).isTrue();

        });
    }

    @Then("I should be able to extract response body from callback for bill paid after timeout")
    public void iShouldBeAbleToExtractResponseBodyFromCallbackForBillPaidAfterTimeout() {
        await().atMost(awaitMost, SECONDS).pollDelay(pollDelay, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            boolean flag = false;
            List<ServeEvent> allServeEvents = getAllServeEvents();
            for (int i = allServeEvents.size() - 1; i >= 0; i--) {
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
                    if (rootNode != null && rootNode.has("code") && rootNode.get("code").asText().equals("01")) {
                        if (rootNode.get("requestID").asText().equals(scenarioScopeState.clientCorrelationId)) {
                            String reason = null;
                            if (rootNode.has("reason")) {
                                reason = rootNode.get("reason").asText();
                            }
                            assertThat(reason).isNotEmpty();
                            assertThat(reason).contains("Bill Payment Failed: Bill Paid After Timeout");
                            String code = null;
                            if (rootNode.has("code")) {
                                code = rootNode.get("code").asText();
                            }
                            assertThat(code).isNotEmpty();
                        }
                    }

                }

            }
            assertThat(flag).isTrue();
        });
    }

    @Then("I can create DTO for Biller RTP Request without alias details")
    public void iCanCreateDTOForBillerRTPRequestWithoutAliasDetails() {
        Bill bill = new Bill("Test", 100.0);
        billRTPReqDTO = new BillRTPReqDTO("123445", billId, "00", new Alias(), bill);
    }

    @Then("I can create DTO for Biller RTP Request with incorrect rtp type")
    public void iCanCreateDTOForBillerRTPRequestWithIncorrectRtpType() {
        Bill bill = new Bill("Test", 100.0);
        billRTPReqDTO = new BillRTPReqDTO("123445", billId, "03", new Alias(), bill);
    }

    @Then("I can create DTO for Biller RTP Request with incorrect rtp information")
    public void iCanCreateDTOForBillerRTPRequestWithIncorrectRtpInformation() {
        Bill bill = new Bill("Test", 100.0);
        PayerFSPDetail payerFSPDetail = new PayerFSPDetail("lion", "1223455");
        billRTPReqDTO = new BillRTPReqDTO("123445", billId, "01", payerFSPDetail, bill);
    }

    @Then("I can create DTO for Biller RTP Request with incorrect alias details")
    public void iCanCreateDTOForBillerRTPRequestWithIncorrectAliasDetails() {
        Bill bill = new Bill("Test", 100.0);
        billRTPReqDTO = new BillRTPReqDTO("123445", billId, "03", new Alias("05", "12345"), bill);
    }

    @Then("I can create DTO for Biller RTP Request to mock payer fi unreachable")
    public void iCanCreateDTOForBillerRTPRequestToMockPayerFiUnreachable() {
        Bill bill = new Bill("Test", 100.0);
        PayerFSPDetail payerFSPDetail = new PayerFSPDetail("rhino", "122333");
        billRTPReqDTO = new BillRTPReqDTO("123445", billId, "00", payerFSPDetail, bill);
    }

    @Then("I can create DTO for Biller RTP Request to mock payer fsp failed to debit amount")
    public void iCanCreateDTOForBillerRTPRequestToMockPayerFspFailedToDebitAmount() {
        Bill bill = new Bill("Test", 100.0);
        PayerFSPDetail payerFSPDetail = new PayerFSPDetail("rhino", "1223334444");
        billRTPReqDTO = new BillRTPReqDTO("123445", billId, "00", payerFSPDetail, bill);
    }

    @And("I can extract the error from response body and assert the error information as {string}")
    public void iCanExtractTheErrorFromResponseBodyAndAssertTheErrorInformationAs(String errorMessage) {
        PhErrorDTO errorInformation;
        try {
            JSONObject jsonObject = new JSONObject(scenarioScopeState.response);
            errorInformation = objectMapper.readValue(jsonObject.toString(), PhErrorDTO.class);

        } catch (JSONException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        assertThat(errorInformation.getErrors().get(0).getErrorDescription()).isEqualTo(errorMessage);
    }

    @And("I can extract the error from callback body and assert error message as {string}")
    public void iCanExtractTheErrorFromCallbackBodyAndAssertErrorMessageAs(String errorMessage) {
        await().atMost(awaitMost, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            boolean flag = false;
            List<ServeEvent> allServeEvents = getAllServeEvents();
            for (int i = allServeEvents.size() - 1; i >= 0; i--) {
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
                    if (rootNode != null && rootNode.has("errorMessage")) {
                        if (request.getRequest().getHeader("X-Client-Correlation-ID").equals(scenarioScopeState.clientCorrelationId)) {
                            String error = null;
                            if (rootNode.has("errorMessage")) {
                                error = rootNode.get("errorMessage").asText();
                            }
                            assertThat(error).isEqualTo(errorMessage);
                        }
                    }
                }
            }
        });
    }

    @Then("I should be able to extract response body from callback for bill notification with empty bill id")
    public void iShouldBeAbleToExtractResponseBodyFromCallbackForBillNotificationWithEmptyBillId() throws JSONException {
        await().atMost(awaitMost, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {

            JSONObject jsonObject = new JSONObject(scenarioScopeState.response);
            scenarioScopeState.transactionId = jsonObject.getString("transactionId");
            assertThat(scenarioScopeState.transactionId.equals("Invalid Request: Bill Id Empty")).isTrue();
        });
    }
}
