package org.mifos.integrationtest.cucumber.stepdef;

import com.google.gson.Gson;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;import org.json.JSONObject;
import org.mifos.integrationtest.common.CollectionHelper;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.CollectionResponse;import org.mifos.integrationtest.common.dto.operationsapp.GetTransactionRequestResponse;import org.mifos.integrationtest.config.ChannelConnectorConfig;
import java.util.UUID;import static com.google.common.truth.Truth.assertThat;

public class ChannelCollectionStepDef extends BaseStepDef {

    @And("I have the request body with payer ams identifier keys as {string} and {string}")
    public void iHaveRequestBody(String key1, String key2) throws JSONException {
        JSONObject collectionRequestBody = CollectionHelper.getCollectionRequestBody(key1, key2);
        BaseStepDef.requestBody = collectionRequestBody;
        logger.info(String.valueOf(BaseStepDef.requestBody));
    }

    @When("I call the channel collection API with client correlation id and expected status of {int}")
    public void iCallChannelCollectionAPI(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        requestSpec.header(Utils.X_CORRELATIONID, UUID.randomUUID());
        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(channelConnectorConfig.channelConnectorContactPoint)
//          BaseStepDef.response = RestAssured.given(requestSpec).baseUri("https://localhost:8443")
                .body(BaseStepDef.requestBody.toString()).expect().spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when().post(channelConnectorConfig.collectionEndpoint).andReturn().asString();
    }

    @Then("I should get transaction id in response")
    public void iGetTransactionIdInResponse(){
        CollectionResponse response = (new Gson()).fromJson(BaseStepDef.response, CollectionResponse.class);
        BaseStepDef.transactionId = response.getTransactionId();
        assertThat(response.getTransactionId()).isNotNull();
    }

    @Then("I should get transactionId with null value in response")
    public void iGetErrorInResponse(){
        CollectionResponse response = (new Gson()).fromJson(BaseStepDef.response, CollectionResponse.class);
        assertThat(response.getTransactionId()).isNull();
    }

    @When("I call the get txn API in ops app with transactionId as parameter")
    public void iCallTheTxnAPIWithTransactionId()throws InterruptedException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + BaseStepDef.accessToken);
        }
        requestSpec.queryParam("transactionId", BaseStepDef.transactionId);

        Thread.sleep(10000);

        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .get(operationsAppConfig.transactionRequestsEndpoint).andReturn().asString();

        logger.info("GetTxn Request Response: " + BaseStepDef.response);
    }


    @Then("I should get transaction state as completed and externalId not null")
    public void assertValues() {
        GetTransactionRequestResponse transactionRequestResponse = (new Gson()).fromJson(BaseStepDef.response, GetTransactionRequestResponse.class);
        assertThat(transactionRequestResponse.getContent().size()).isEqualTo(1);
        assertThat(transactionRequestResponse.getContent().get(0).getState()).isAnyOf ("ACCEPTED", "FAILED");
        assertThat(transactionRequestResponse.getContent().get(0).getExternalId()).isNotNull();
    }

}
