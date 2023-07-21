package org.mifos.integrationtest.common;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class Utils {

    public static final String TENANT_PARAM_NAME = "Platform-TenantId";
    public static final String REQUEST_TYPE_PARAM_NAME = "requestType";
    public static final String DEFAULT_TENANT = "gorilla";
    public static final String X_CORRELATIONID = "X-CorrelationID";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_VALUE = "application/json";
    public static final String HEADER_JWS_SIGNATURE = "X-SIGNATURE";
    public static final String HEADER_FILENAME = "filename";
    public static final String HEADER_PURPOSE = "purpose";
    public static final String QUERY_PARAM_TYPE = "type";

    public static void initializeRESTAssured() {
        RestAssured.baseURI = "https://localhost";
        RestAssured.port = 8443;
    }

    public static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            System.out.println("Unexpected InterruptedException" + e);
            throw new IllegalStateException("Unexpected InterruptedException", e);
        }
    }

    public static RequestSpecification getDefaultSpec() {
        RequestSpecification requestSpec = new RequestSpecBuilder().build();
        requestSpec.relaxedHTTPSValidation();
        return requestSpec;
    }

    public static RequestSpecification getDefaultSpec(String tenant, String clientCorrelationId) {
        RequestSpecification requestSpec = getDefaultSpec();
        requestSpec.header(TENANT_PARAM_NAME, tenant);
        requestSpec.header(X_CORRELATIONID, clientCorrelationId);
        requestSpec.header(CONTENT_TYPE, "application/json");
        return requestSpec;
    }

    public static RequestSpecification getDefaultSpec(String tenant) {
        RequestSpecification requestSpec = getDefaultSpec();
        requestSpec.header(TENANT_PARAM_NAME, tenant);
        // requestSpec.header(X_CORRELATIONID, "123456789");
        requestSpec.header(CONTENT_TYPE, "application/json");
        return requestSpec;
    }

    public static String getAbsoluteFilePathToResource(String fileName) {
        return "src/test/java/resources/" + fileName;
    }

    public static RequestSpecification getRequestType(String requestType) {
        RequestSpecification requestSpec = getDefaultSpec();
        requestSpec.header(REQUEST_TYPE_PARAM_NAME, requestType);
        return requestSpec;
    }

}
