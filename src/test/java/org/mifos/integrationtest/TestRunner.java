package org.mifos.integrationtest;

import courgette.api.CourgetteOptions;
import courgette.api.CourgetteRunLevel;
import courgette.api.CourgetteTestOutput;
import courgette.api.CucumberOptions;
import courgette.api.junit.Courgette;
import org.junit.runner.RunWith;

//@RunWith(Cucumber.class)
//@CucumberOptions(features = { "src/test/java/resources" }, glue = { "org.mifos.integrationtest.cucumber" }, plugin = {
//        "html:cucumber-report", "json:cucumber.json", "pretty", "html:build/cucumber-report.html", "json:build/cucumber-report.json",
//        "junit:build/cucumber.xml" })

@RunWith(Courgette.class)
@CourgetteOptions(threads = 4, runLevel = CourgetteRunLevel.FEATURE, rerunFailedScenarios = false,
        testOutput = CourgetteTestOutput.CONSOLE,

        reportTitle = "Paymenthub Test results", reportTargetDir = "build", cucumberOptions = @CucumberOptions(features = "src/test/java/resources", glue = "org.mifos.integrationtest.cucumber", tags = "@gov", publish = true, plugin = {
                "html:cucumber-report", "json:cucumber.json", "pretty", "html:build/cucumber-report.html",
                "json:build/cucumber-report.json", "junit:build/cucumber.xml" }))
public class TestRunner {}
