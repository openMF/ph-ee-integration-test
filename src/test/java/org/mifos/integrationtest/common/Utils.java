package org.mifos.integrationtest.common;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class Utils {

    public static final String TENANT_PARAM_NAME = "Platform-TenantId";
    public static final String DEFAULT_TENANT = "ibank-india";

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

    public static RequestSpecification getDefaultSpec(String tenant) {
        RequestSpecification requestSpec = getDefaultSpec();
        requestSpec.header(TENANT_PARAM_NAME, tenant);
        return requestSpec;
    }

    public static String getAbsoluteFilePathToResource(String fileName) {
        return "src/test/java/resources/" +
                fileName;
    }

}
