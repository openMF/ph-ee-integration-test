package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.integrationtest.common.CollectionHelper;
import org.mifos.integrationtest.common.Utils;

public class CollectionPaymentSchemeDef extends BaseStepDef {

    @When("I call the collection endpoint with payment scheme {string} and status is {int}")
    public void iCallTheCollectionEndpointWithPaymentSchemeAndExpectedStatus(String paymentScheme, int expectedStatus)
            throws JSONException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        requestSpec.header(Utils.X_CORRELATIONID, BaseStepDef.clientCorrelationId);
        requestSpec.header(Utils.PAYMENT_SCHEME_HEADER, paymentScheme);
        JSONObject collectionRequestBody = CollectionHelper.getCollectionRequestBody("1", "254708374149", "24450523");
        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(collectionRequestBody.toString()).expect().spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when().post(channelConnectorConfig.collectionEndpoint).andReturn().asString();
    }
}
