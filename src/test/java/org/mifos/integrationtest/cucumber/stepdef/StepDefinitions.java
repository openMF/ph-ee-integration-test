package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class StepDefinitions {

    @Autowired
    private ScenarioScopedBean scenarioScopedBean;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Given("I set the variable to {string}")
    public void setVariable(String value) {
        scenarioScopedBean.setData(value);
    }

    @Then("I retrieve the variable")
    public void retrieveVariable() {
        String retrievedData = scenarioScopedBean.getData();
        logger.info("Retrieved data: {}", retrievedData);
        // You can log or assert the retrievedData as needed
    }
}
