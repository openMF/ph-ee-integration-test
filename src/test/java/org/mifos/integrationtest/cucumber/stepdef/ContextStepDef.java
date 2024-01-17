package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class ContextStepDef extends BaseStepDef {

    @Autowired
    private ApplicationContext applicationContext;

    @Given("I can autowire the object mapper bean")
    public void checkIfObjectMapperIsInjected() {
        assertThat(objectMapper).isNotNull();
    }

    @Then("Application context should be not null")
    public void checkIfApplicationContextIsStarted() {
        assertThat(this.applicationContext).isNotNull();
    }

}
