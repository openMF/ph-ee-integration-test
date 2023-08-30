package org.mifos.integrationtest;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mifos.integrationtest.common.CollectionHelper;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.CollectionResponse;
import org.mifos.integrationtest.common.dto.operationsapp.GetTransactionRequestResponse;
import org.mifos.integrationtest.common.dto.operationsapp.TransactionRequest;
import org.mifos.integrationtest.config.ChannelConnectorConfig;
import org.mifos.integrationtest.config.OperationsAppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static com.google.common.truth.Truth.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClientCorrelationIdTest {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private ResponseSpecification statusOkResponseSpec;
    private RequestSpecification requestSpec;

    private String transactionId = "fce838977c90oKNEILYY";
    private String clientCorrelationId = "123456789";

    @BeforeAll
    public void setup() {
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header(Utils.TENANT_PARAM_NAME, Utils.DEFAULT_TENANT);
        this.statusOkResponseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }

    @Autowired
    OperationsAppConfig operationsAppConfig;

    @Autowired
    ChannelConnectorConfig channelConnectorConfig;

    @Test
    @Disabled
    public void testSendCollectionRequest() throws JSONException {
        requestSpec.header(Utils.X_CORRELATIONID, clientCorrelationId);
        JSONObject collectionRequestBody = CollectionHelper.getCollectionRequestBody("1", "254708374149", "24450523");
        logger.info(String.valueOf(collectionRequestBody));
        String json = RestAssured.given(requestSpec).baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(collectionRequestBody.toString()).expect().spec(statusOkResponseSpec).when().post("/channel/collection").andReturn()
                .asString();
        CollectionResponse response = (new Gson()).fromJson(json, CollectionResponse.class);
        assertThat(response.getTransactionId()).isNotEmpty();
        logger.debug(response.getTransactionId());
        this.transactionId = response.getTransactionId();
    }

    @Test
    @Disabled
    public void testGetTransactionRequestApi() {
        Utils.sleep(5);
        logger.info("Getting transactionRequestObject with transactionId {} ", this.transactionId);
        RequestSpecification localSpec = requestSpec;
        localSpec.queryParam("transactionId", this.transactionId);
        String json = RestAssured.given(localSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect().spec(statusOkResponseSpec)
                .when().get("/api/v1/transactionRequests").andReturn().asString();
        GetTransactionRequestResponse transactionRequestResponse = (new Gson()).fromJson(json, GetTransactionRequestResponse.class);
        assertThat(transactionRequestResponse.getContent().size()).isEqualTo(1);
        TransactionRequest transactionRequest = transactionRequestResponse.getContent().get(0);
        this.clientCorrelationId = transactionRequest.getClientCorrelationId();
    }

}
