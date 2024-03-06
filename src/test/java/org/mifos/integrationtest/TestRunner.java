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
@CourgetteOptions(threads = 3, runLevel = CourgetteRunLevel.SCENARIO, rerunFailedScenarios = false,
        // rerunAttempts = ,
        testOutput = CourgetteTestOutput.CONSOLE,

        reportTitle = "Courgette-JVM Example", reportTargetDir = "build", environmentInfo = "browser=chrome; git_branch=master", cucumberOptions = @CucumberOptions(features = "src/test/java/resources", glue = "org.mifos.integrationtest.cucumber", tags = "@gov", publish = true, plugin = {
                "html:cucumber-report", "json:cucumber.json", "pretty", "html:build/cucumber-report.html",
                "json:build/cucumber-report.json", "junit:build/cucumber.xml" }))
public class TestRunner {}
