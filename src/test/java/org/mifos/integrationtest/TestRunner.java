package org.mifos.integrationtest;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/java/resources"},
        glue = {"org.mifos.integrationtest.cucumber"},
        plugin = {
            "html:cucumber-report",
            "json:cucumber.json",
            "pretty",
            "html:build/cucumber-report.html",
            "json:build/cucumber-report.json"
        }
)
public class TestRunner {
}
