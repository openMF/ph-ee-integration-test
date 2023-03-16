package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import static com.google.common.truth.Truth.assertThat;

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
