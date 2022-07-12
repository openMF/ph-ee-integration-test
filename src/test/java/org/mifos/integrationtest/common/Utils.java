package org.mifos.integrationtest.common;

import io.restassured.RestAssured;

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

}
