package org.mifos.integrationtest.cucumber.stepdef;

import com.google.gson.Gson;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.BatchSummaryResponse;
import org.mifos.integrationtest.config.BulkProcessorConfig;
import org.mifos.integrationtest.config.OperationsAppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static com.google.common.truth.Truth.assertThat;

public class BulkPaymentStepDef extends BaseStepDef {

    private String batchId;

    private int completionPercent;

    @Value("${config.completion-threshold-check.completion-threshold}")
    private int thresholdPercent;

    @Autowired
    BulkProcessorConfig bulkProcessorConfig;

    @Autowired
    OperationsAppConfig operationsAppConfig;

    @Given("the CSV file is available")
    public boolean isCsvFileAvailable(){
        String fileName = "bulk-payment.csv";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(fileName);
        return resource != null && resource.getPath().endsWith(".csv");
    }

    @When("initiate the batch transaction API with the input CSV file with tenant as {string}")
    public void initiateTheBatchTransactionAPIWithTheInputCSVFileWithTenantAs(String tenant) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Purpose", "test payment");
        headers.put("filename", "ph-ee-bulk-demo-6.csv");
        headers.put("X-CorrelationID", "12345678-6897-6798-6798-098765432134");
        headers.put("Platform-TenantId", tenant);
        String fileContent = getFileContent("bulk-payment.csv");
        logger.info("file content: " + fileContent);
        RequestSpecification requestSpec = getDefaultSpec();
        String response =  RestAssured.given(requestSpec)
                .baseUri(bulkProcessorConfig.bulkProcessorContactPoint)
                .multiPart(getMultiPart(fileContent))
                .queryParam("type", "csv")
                .headers(headers)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(bulkProcessorConfig.bulkTransactionEndpoint)
                .andReturn().asString();
        batchId = fetchBatchId(response);
        logger.info(batchId);
        logger.info("Batch transaction API response: " + response);
    }

    @Given("the batch ID for the submitted CSV file")
    public void isBatchIdAvailable(){
        assertThat(batchId).isNotEmpty();
    }

    @And("poll the batch summary API using the batch ID and tenant as {string}")
    public void pollTheBatchSummaryAPIUsingTheBatchIDAndTenantAs(String tenant) {
        int retries = 5;
        int intervalInSeconds = 30;

        Map<String, String> headers = new HashMap<>();
        headers.put("Platform-TenantId", tenant);
        RequestSpecification requestSpec = getDefaultSpec();

        for(int index = 0; index < retries; index++) {
            String response =  RestAssured.given(requestSpec)
                    .baseUri(operationsAppConfig.operationAppContactPoint)
                    .param("batchId", batchId)
                    .headers(headers)
                    .expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                    .when()
                    .get(operationsAppConfig.batchSummaryEndpoint)
                    .andReturn().asString();
            Gson gson = new Gson();
            BatchSummaryResponse batchSummaryResponse = gson.fromJson(response, BatchSummaryResponse.class);
            assertThat(batchSummaryResponse).isNotNull();

            if(batchSummaryResponse.getTotal() != 0){
                completionPercent = (int) (batchSummaryResponse.getSuccessful()/ batchSummaryResponse.getTotal() * 100);
            }
            Utils.sleep(intervalInSeconds);
        }
    }

    @Then("successful transactions percentage should be greater than or equal to minimum threshold")
    public void batchSummarySuccessful(){
        assertThat(completionPercent).isNotNull();
        assertThat(completionPercent).isGreaterThan(thresholdPercent);
    }

    private static RequestSpecification getDefaultSpec() {
        RequestSpecification requestSpec = new RequestSpecBuilder().build();
        requestSpec.relaxedHTTPSValidation();
        return requestSpec;
    }

    private MultiPartSpecification getMultiPart(String fileContent) {
        return new MultiPartSpecBuilder(fileContent.getBytes()).
                fileName("test.csv").
                controlName("file").
                mimeType("text/plain").
                build();
    }

    private String getFileContent(String filePath) {
        File file = new File(filePath);
        Reader reader;
        CSVFormat csvFormat;
        CSVParser csvParser = null;
        try {
            reader = new FileReader(file);
            csvFormat = CSVFormat.DEFAULT.withDelimiter(',');
            csvParser = new CSVParser(reader, csvFormat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        StringJoiner stringJoiner = new StringJoiner("\n");

        for (CSVRecord csvRecord : csvParser) {
            stringJoiner.add(csvRecord.toString());
        }
        return stringJoiner.toString();
    }

    private String fetchBatchId(String response) {
        String[] split = response.split(",");
        return split[0].substring(31);
    }
}
