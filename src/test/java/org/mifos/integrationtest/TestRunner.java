package org.mifos.integrationtest;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;

import courgette.api.CourgetteOptions;
import courgette.api.CourgetteRunLevel;
import courgette.api.CourgetteTestOutput;
import courgette.api.CucumberOptions;
import courgette.api.junit.Courgette;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mifos.integrationtest.config.MockServer;
import org.springframework.beans.factory.annotation.Autowired;

//@RunWith(Cucumber.class)
//@CucumberOptions(features = { "src/test/java/resources" }, glue = { "org.mifos.integrationtest.cucumber" }, plugin = {
//        "html:cucumber-report", "json:cucumber.json", "pretty", "html:build/cucumber-report.html", "json:build/cucumber-report.json",
//        "junit:build/cucumber.xml" })

@RunWith(Courgette.class)
@CourgetteOptions(threads = 3, runLevel = CourgetteRunLevel.FEATURE, rerunFailedScenarios = false, testOutput = CourgetteTestOutput.CONSOLE,

        reportTitle = "Paymenthub Test results", reportTargetDir = "build", cucumberOptions = @CucumberOptions(features = "src/test/java/resources", glue = "org.mifos.integrationtest.cucumber", tags = "@gov", publish = true, plugin = {
                "html:cucumber-report", "json:cucumber.json", "pretty", "html:build/cucumber-report.html",
                "json:build/cucumber-report.json", "junit:build/cucumber.xml" }))
@SuppressWarnings({ "FinalClass", "HideUtilityClassConstructor" })
public class TestRunner {

    @Autowired
    public static MockServer mockServer;

    @BeforeClass
    public static void setup() {
        mockServer.getMockServer().start();
        configureFor(53013);
    }

    @AfterClass
    public static void teardown() {
        mockServer.getMockServer().stop();
    }
}
