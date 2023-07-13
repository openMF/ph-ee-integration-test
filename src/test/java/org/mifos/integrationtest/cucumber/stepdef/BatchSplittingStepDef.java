package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static com.google.common.truth.Truth.assertThat;

public class BatchSplittingStepDef extends BaseStepDef {

    private int subbatchSize;
    private int totalTransactions;
    private String firstTransactionSubbatchId;
    private String lastTransactionSubbatchId;

    @Given("the system has a configured subbatch size of {int} transactions")
    public void setSubbatchSize(int subbatchSize) {
        this.subbatchSize = subbatchSize;
    }

    @When("the batch is split into subbatches")
    public void splitBatchIntoSubbatches() {
        // Logic to split the batch into subbatches based on the subbatch size - fire batch txn api with csv file
        // Store the request ID of the first and last transactions
        firstTransactionSubbatchId = getSubbatchIdForTransaction(1);
        lastTransactionSubbatchId = getSubbatchIdForTransaction(totalTransactions);
    }

    @Then("the subbatch ID for the first transaction should be different from the subbatch ID of the last transaction within the boundary of the subbatch size config")
    public void assertSubbatchIds() {
        // Assertion logic to check if the first and last transaction subbatch IDs are different
        // within the boundary of the subbatch size config
        assertThat(firstTransactionSubbatchId).isNotEqualTo(lastTransactionSubbatchId);
    }

    @Then("the subbatch IDs for transactions within the same subbatch should be the same")
    public void assertSameSubbatchIds() {
        // Assertion logic to check if the subbatch IDs for transactions within the same subbatch are the same
        assertThat(getSubbatchIdForTransaction(2)).isEqualTo(firstTransactionSubbatchId);
    }

    private String getSubbatchIdForTransaction(int transactionNumber) {
        // Logic to retrieve the subbatch ID for the given transaction number
        // This can be done by accessing the data or the processing logic used to split the batch
        // Return the corresponding subbatch ID
        return "subbatchID"; // Replace with actual implementation
    }





}
