package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static com.google.common.truth.Truth.assertThat;
import static org.mifos.integrationtest.common.Utils.getDefaultSpec;

public class BatchSplittingStepDef extends BaseStepDef {

    private int subbatchSize;
    private int totalTransactions;
    private String firstTransactionSubbatchId;
    private String lastTransactionSubbatchId;

    @Given("the csv file {string} is available")
    public void theCsvFileIsAvailable(String fileName) {
        BaseStepDef.filename = fileName;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(filename);
        assertThat(filename).isNotEmpty();
    }

    @And("the system has a configured subbatch size of {int} transactions")
    public void setSubbatchSize(int subbatchSize) {
        this.subbatchSize = subbatchSize;
    }

    @And("the first and last transactions from the CSV file are fetched")
    public void theFirstAndLastTransactionsFromTheCSVFileAreFetched() {
        String fileContent = getFileContent(filename);
        logger.info(fileContent);
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

        String response = RestAssured.given(requestSpec)
                .baseUri("http://localhost:5002")
                .multiPart(getMultiPart(fileContent))
                .queryParam("type", "csv")
                .headers(headers)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post("/batchtransactions")
                .andReturn().asString();

        batchId = fetchBatchId(response);
        logger.info("Batch transaction API response: " + response);
    }

    @Then("the sub batch IDs for the given request ID are retrieved")
    public void theSubBatchIDsForTheGivenRequestIDAreRetrieved() {
    }

    @And("the sub batch IDs for the first and last transactions should be different")
    public void theSubBatchIDsForTheFirstAndLastTransactionsShouldBeDifferent() {
        assertThat(firstTransactionSubbatchId).isNotEqualTo(lastTransactionSubbatchId);
    }

    private MultiPartSpecification getMultiPart(String fileContent) {
        return new MultiPartSpecBuilder(fileContent.getBytes()).
                fileName("test.csv").
                controlName("file").
                mimeType("text/plain").
                build();
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

        for (CSVRecord csvRecord : csvParser) {
            stringJoiner.add(csvRecord.toString());
        }
        return stringJoiner.toString();
    }
}
