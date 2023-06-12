package org.mifos.integrationtest.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mifos.integrationtest.IntegrationTestApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@CucumberContextConfiguration
@CucumberOptions(publish = true)
@SpringBootTest
public class CucumberContext {

    @Test
    void contextLoads() {
    }

}
