package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

import static com.google.common.truth.Truth.assertThat;

@Slf4j
public class OperationsStepDef extends BaseStepDef {

    @Given("I am happy")
    public void happy() {
        assertThat(1).isEqualTo(1);
    }

    @When("I get chocolate")
    public void whenHappy() {
        assertThat(1).isEqualTo(1);
    }

    @Then("I get more happy")
    public void moreHappy() {
        assertThat(1).isEqualTo(1);
    }

    @Before("@ops-batch-setup")
    public void operationsBatchTestSetup() {
        batchDbSetup();
        log.info("Running @ops-batch-setup");
    }

    @After("@ops-batch-teardown")
    public void operationsBatchTestTearDown() {
        batchDbTearDown();
        log.info("Running @ops-batch-teardown");
    }

    private void batchDbSetup() {
		// todo define the setup step/task in this method
        assertThat(1).isEqualTo(1);
    }

    private void batchDbTearDown() {
        // todo define the teardown step/task in this method
        assertThat(1).isEqualTo(1);
    }

}
