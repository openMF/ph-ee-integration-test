package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;
import static org.mifos.integrationtest.common.Utils.getDefaultSpec;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.mifos.integrationtest.common.Batch;
import org.mifos.integrationtest.common.BatchPage;
import org.mifos.integrationtest.config.BulkProcessorConfig;
import org.mifos.integrationtest.config.OperationsAppConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class BatchSplittingStepDef extends BaseStepDef {

    private int subBatchSize;

    private int totalTransactionCount;

    private int expectedSubBatchCount;

    private int actualSubBatchCount;

    @Autowired
    private BulkProcessorConfig bulkProcessorConfig;

    @Autowired
    private OperationsAppConfig operationsAppConfig;

    @Given("the csv file {string} is available")
    public void theCsvFileIsAvailable(String fileName) {
        BaseStepDef.filename = fileName;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(filename);
        assertThat(filename).isNotEmpty();
    }

    @And("the system has a configured sub batch size of {int} transactions")
    public void setSubBatchSize(int subBatchSize) {
        this.subBatchSize = subBatchSize;
    }

    @When("the batch transaction API is initiated with the uploaded file")
    public void theBatchTransactionAPIIsInitiatedWithTheUploadedFile() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Purpose", "test payment");
        headers.put("filename", filename);
        headers.put("X-CorrelationID", "12345678-6897-6798-6798-098765432134");
        headers.put("Platform-TenantId", tenant);

        String fileContent = getFileContent(filename);
        RequestSpecification requestSpec = getDefaultSpec();
        String response = RestAssured.given(requestSpec).baseUri(bulkProcessorConfig.bulkProcessorContactPoint).multiPart(getMultiPart(fileContent))
                .queryParam("type", "csv").headers(headers).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(bulkProcessorConfig.bulkTransactionEndpoint).andReturn().asString();

        batchId = fetchBatchId(response);
        logger.info("Batch transaction API response: " + response);
    }

    @And("the expected sub batch count is calculated")
    public void theExpectedSubBatchCountIsCalculated() {
        expectedSubBatchCount = totalTransactionCount % subBatchSize == 0 ? totalTransactionCount / subBatchSize
                : (totalTransactionCount / subBatchSize) + 1;
    }

    @Then("the actual sub batch count is calculated from the response")
    public void theActualSubBatchCountIsCalculatedFromTheResponse() {
        List<Batch> batchList = null;
        Map<String, String> headers = new HashMap<>();
        headers.put("batchId", batchId);

        RequestSpecification requestSpec = getDefaultSpec();
        String response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint)
                .queryParam("batchId", batchId).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .get(operationsAppConfig.getAllBatchesEndpoint).andReturn().asString();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            BatchPage batchPage = objectMapper.readValue(response, new TypeReference<BatchPage>() {});
            batchList = batchPage.getContent();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        actualSubBatchCount = batchList.size();
    }

    @And("the expected sub batch count and actual sub batch count should be equal")
    public void theExpectedSubBatchCountAndActualSubBatchCountShouldBeEqual() {
        assertThat(actualSubBatchCount).isEqualTo(expectedSubBatchCount);
    }

    private MultiPartSpecification getMultiPart(String fileContent) {
        return new MultiPartSpecBuilder(fileContent.getBytes()).fileName("test.csv").controlName("file").mimeType("text/plain").build();
    }

    private String fetchBatchId(String response) {
        String[] split = response.split(",");
        return split[0].substring(31);
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

        int count = 0;
        for (CSVRecord csvRecord : csvParser) {
            stringJoiner.add(csvRecord.toString());
            count++;
        }
        totalTransactionCount = count;
        return stringJoiner.toString();
    }
}
