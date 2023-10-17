package org.mifos.integrationtest.common;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static final String TENANT_PARAM_NAME = "Platform-TenantId";
    public static final String REQUEST_TYPE_PARAM_NAME = "requestType";
    public static final String DEFAULT_TENANT = "gorilla";
    public static final String X_CORRELATIONID = "X-CorrelationID";
    public static final String X_CallbackURL = "X-CallbackURL";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_VALUE = "application/json";
    public static final String HEADER_JWS_SIGNATURE = "X-SIGNATURE";
    public static final String HEADER_FILENAME = "filename";
    public static final String HEADER_PURPOSE = "purpose";
    public static final String QUERY_PARAM_TYPE = "type";
    public static final String HEADER_REGISTERING_INSTITUTE_ID = "X-Registering-Institution-ID";
    public static final String HEADER_PROGRAM_ID = "X-Program-ID";

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
//         requestSpec.header(X_CORRELATIONID, "123456789");
        requestSpec.header(CONTENT_TYPE, "application/json");
        return requestSpec;
    }

    public static String getAbsoluteFilePathToResource(String fileName) {
        return "src/test/java/resources/batch_demo_csv/" + fileName;
    }

    public static RequestSpecification getRequestType(String requestType) {
        RequestSpecification requestSpec = getDefaultSpec();
        requestSpec.header(REQUEST_TYPE_PARAM_NAME, requestType);
        return requestSpec;
    }

    public static String getUTCFormat(String dateTime, String interfaceTimezone) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        ZoneId interfaceZone = ZoneId.of(interfaceTimezone);
        ZonedDateTime interfaceDateTime = ZonedDateTime.of(localDateTime, interfaceZone);
        ZonedDateTime gmtDateTime = interfaceDateTime.withZoneSameInstant(ZoneId.of("GMT"));
        DateTimeFormatter gmtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String gmtDateTimeString = gmtDateTime.format(gmtFormatter);
        return gmtDateTimeString;
    }

    public static List<String> extractClientCorrelationIdFromCsv(String filename, List<String> requestIds) {
        File file = new File(Utils.getAbsoluteFilePathToResource(filename));
        try {
            InputStream inputStream = new FileInputStream(file);
            List<String> line = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
            line.remove(0);
            while (line.size() > 0) {
                String[] temp = line.get(0).split(",");
                requestIds.add(temp[1]);
                line.remove(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestIds;
    }

}
