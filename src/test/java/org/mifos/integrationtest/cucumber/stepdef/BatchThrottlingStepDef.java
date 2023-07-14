package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.StringJoiner;

import static com.google.common.truth.Truth.assertThat;

public class BatchThrottlingStepDef extends BaseStepDef {

    private int throttleTimeInSeconds;
    private long startTimeOfSecondSubBatch;

    private long startTimeOfFirstSubBatch;

    private String firstBatchFirstTxn;

    private String secondBatchFirstTxn;

    @And("the system has a configured throttle time of {int} seconds")
    public void theSystemHasAConfiguredThrottleTimeOfSeconds(int throttleTimeInSeconds) {
        this.throttleTimeInSeconds = throttleTimeInSeconds;
        assertThat(this.throttleTimeInSeconds).isGreaterThan(0);
    }

    @And("the first transactions are fetched from consecutive sub batches based on sub batch size of {int} transactions")
    public void theFirstTransactionsAreFetchedFromConsecutiveSubBatchesBasedOnSubBatchSizeOfTransactions(int batchSize) {
        String fileContent = getFileContent(filename);
        String[] firstTxnFromFirstAndSecondSubBatch = getFirstTxnFromFirstAndSecondSubBatch(fileContent, batchSize);
        firstBatchFirstTxn = firstTxnFromFirstAndSecondSubBatch[0];
        secondBatchFirstTxn = firstTxnFromFirstAndSecondSubBatch[1];
    }

    private String[] getFirstTxnFromFirstAndSecondSubBatch(String fileContent, int batchSize){
        String[] csvRecords = fileContent.split("\n");

        String firstBatchFirstTxnRecord = csvRecords[1];
        String secondBatchFirstTxnRecord = csvRecords[1+batchSize];

        String[] firstBatchFirstTxnRecordValues = firstBatchFirstTxnRecord.split(",");
        String[] secondBatchFirstTxnRecordValues = secondBatchFirstTxnRecord.split(",");

        return new String[]{firstBatchFirstTxnRecordValues[1], secondBatchFirstTxnRecordValues[1]};
    }

    @Then("the start time for the consecutive sub batch IDs are retrieved")
    public void theStartTimeForTheConsecutiveSubBatchIDsAreRetrieved() {

    }

    @And("the difference between start time of first sub batch and second sub batch should be greater than or equal to throttle configuration")
    public void theDifferenceBetweenStartTimeOfFirstSubBatchAndSecondSubBatchShouldBeGreaterThanOrEqualToThrottleConfiguration() {
        long timeGapBetweenTwoSubBatches = startTimeOfSecondSubBatch - startTimeOfFirstSubBatch;
        assertThat(timeGapBetweenTwoSubBatches).isGreaterThan(throttleTimeInSeconds*1000);
    }

    // Helper method to get the current time in milliseconds
    private long getCurrentTime() {
        return System.currentTimeMillis();
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
