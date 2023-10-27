package org.mifos.integrationtest;

import static com.google.common.truth.Truth.assertThat;

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
import org.mifos.integrationtest.common.dto.OperationsHelper;
import org.mifos.integrationtest.common.dto.operationsapp.GetTransactionRequestResponse;
import org.mifos.integrationtest.common.dto.operationsapp.TransactionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExternalIdTest {

    private static final Logger log = LoggerFactory.getLogger(ExternalIdTest.class);
    private ResponseSpecification statusOkResponseSpec;
    private RequestSpecification requestSpec;

    private String transactionId = "fce838977c90oKNEILYY";
    private String externalId = "123";

    @BeforeAll
    public void setup() {
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header(Utils.TENANT_PARAM_NAME, Utils.DEFAULT_TENANT);
        this.statusOkResponseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }

    @Test
    @Disabled
    public void testSendCollectionRequest() throws JSONException {
        JSONObject collectionRequestBody = CollectionHelper.getCollectionRequestBody("1", "254708374149", "24450523");
        log.debug("{}", collectionRequestBody);
        String json = RestAssured.given(requestSpec).baseUri("http://localhost:5002").body(collectionRequestBody.toString()).expect()
                .spec(statusOkResponseSpec).when().post("/channel/collection").andReturn().asString();
        CollectionResponse response = (new Gson()).fromJson(json, CollectionResponse.class);
        assertThat(response.getTransactionId()).isNotEmpty();
        log.debug("{}", response.getTransactionId());
        this.transactionId = response.getTransactionId();
    }

    @Test
    @Disabled
    public void testGetTransactionRequestApi() {
        Utils.sleep(5);
        log.debug("Getting transactionRequestObject with transactionId {}", this.transactionId);
        RequestSpecification localSpec = requestSpec;
        localSpec.queryParam("transactionId", this.transactionId);
        String json = RestAssured.given(localSpec).baseUri("http://localhost:5000").expect().spec(statusOkResponseSpec).when()
                .get("/api/v1/transactionRequests").andReturn().asString();
        GetTransactionRequestResponse transactionRequestResponse = (new Gson()).fromJson(json, GetTransactionRequestResponse.class);
        assertThat(transactionRequestResponse.getContent().size()).isEqualTo(1);
        TransactionRequest transactionRequest = transactionRequestResponse.getContent().get(0);
        this.externalId = transactionRequest.getExternalId();
    }

    @Test
    @Disabled
    public void testBulkFilterApi() throws JSONException {
        Utils.sleep(10);
        log.debug("Executing bulk filter api using externalId {}", this.externalId);
        JSONObject bulkFilterRequestBody = OperationsHelper.getBulkFilterRequestBodyForExternalId(this.externalId);
        log.debug("{}", bulkFilterRequestBody);
        String json = RestAssured.given(requestSpec).baseUri("http://localhost:5000").body(bulkFilterRequestBody.toString()).expect()
                .spec(statusOkResponseSpec).when().post("/api/v1/transactionRequests/export").andReturn().asString();
        log.debug("{}", json);
        assertThat(json.split("\n").length).isEqualTo(2);
    }

}
