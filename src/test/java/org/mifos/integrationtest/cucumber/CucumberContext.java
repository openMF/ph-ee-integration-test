package org.mifos.integrationtest.cucumber;

import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@CucumberOptions(publish = true)
@SpringBootTest
public class CucumberContext {

    @Test
    void contextLoads() {
    }

}
