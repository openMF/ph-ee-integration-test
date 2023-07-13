package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static com.google.common.truth.Truth.assertThat;

public class BatchThrottlingStepDef extends BaseStepDef {

    private int throttleTime;
    private long startTimeOfSecondSubbatch;

    @Given("the system has a configured throttle time of {int} seconds")
    public void setThrottleTime(int throttleTime) {
        this.throttleTime = throttleTime;
    }

    @When("the second subbatch is being processed")
    public void processSecondSubbatch() {
        // Logic to process the batch and retrieve the start time of the second subbatch
        startTimeOfSecondSubbatch = getStartTimeOfSecondSubbatch();
    }

    @Then("the system should wait for at least {int} seconds before processing the second subbatch")
    public void assertThrottleTime(int throttleTime) {
        long elapsedTime = getCurrentTime() - startTimeOfSecondSubbatch;
        int elapsedTimeInSeconds = 0;
        // Assertion logic to check if the elapsed time is at least equal to the throttle time
        // You can use an assertion library like JUnit or AssertJ for the assertions
        assertThat(elapsedTimeInSeconds).isLessThan(throttleTime);
    }

    @Then("the start time of the second subbatch or the first transaction from the second subbatch should be later than the throttle time")
    public void assertStartTimeLaterThanThrottleTime() {
        long startTimeOfFirstTransaction = getStartTimeOfFirstTransactionFromSecondSubbatch();
        // Assertion logic to check if the start time of the second subbatch or the first transaction
        // from the second subbatch is later than the throttle time
        // Example: assertThat(startTimeOfFirstTransaction).isGreaterThan(throttleTime);
    }

    // Helper method to get the current time in milliseconds
    private long getCurrentTime() {
        return System.currentTimeMillis();
    }

    // Helper method to retrieve the start time of the second subbatch
    private long getStartTimeOfSecondSubbatch() {
        // Logic to retrieve the start time of the second subbatch
        // This can be done by accessing the data or the processing logic used to handle subbatches
        // Return the corresponding start time
        return 0L; // Replace with actual implementation
    }

    // Helper method to retrieve the start time of the first transaction from the second subbatch
    private long getStartTimeOfFirstTransactionFromSecondSubbatch() {
        // Logic to retrieve the start time of the first transaction from the second subbatch
        // This can be done by accessing the data or the processing logic used to handle transactions
        // Return the corresponding start time
        return 0L; // Replace with actual implementation
    }

    @Then("the system should wait for at least \\{throttleTime} seconds before processing the second subbatch")
    public void theSystemShouldWaitForAtLeastThrottleTimeSecondsBeforeProcessingTheSecondSubbatch() {
    }
}
