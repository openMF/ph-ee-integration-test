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
import org.mifos.integrationtest.common.dto.CollectionResponse;import org.mifos.integrationtest.config.ChannelConnectorConfig;
import static com.google.common.truth.Truth.assertThat;

public class ChannelCollectionStepDef extends BaseStepDef {

    @And("I have the request body with payer ams identifier keys as {string} and {string}")
    public void iHaveRequestBody(String key1, String key2) throws JSONException {
        JSONObject collectionRequestBody = CollectionHelper.getCollectionRequestBody(key1, key2);
        BaseStepDef.requestBody = collectionRequestBody;
        logger.info(String.valueOf(BaseStepDef.requestBody));
    }

    @When("I call the channel collection API with client correlation id as {int} expected status of {int}")
    public void iCallChannelCollectionAPI(int correlationId, int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        requestSpec.header(Utils.X_CORRELATIONID, correlationId);
        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(BaseStepDef.requestBody.toString()).expect().spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when().post(channelConnectorConfig.collectionEndpoint).andReturn().asString();
    }

    @Then("I should get transaction id in response")
    public void iGetTransactionIdInResponse(){
        CollectionResponse response = (new Gson()).fromJson(BaseStepDef.response, CollectionResponse.class);
        assertThat(response.getTransactionId()).isNotNull();
    }

    @Then("I should get transactionId with null value in response")
    public void iGetErrorInResponse(){
        CollectionResponse response = (new Gson()).fromJson(BaseStepDef.response, CollectionResponse.class);
                assertThat(response.getTransactionId()).isNull();
    }

}
