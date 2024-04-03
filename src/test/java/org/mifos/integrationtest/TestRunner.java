package org.mifos.integrationtest;

import com.github.tomakehurst.wiremock.WireMockServer;
import courgette.api.CourgetteOptions;
import courgette.api.CourgetteRunLevel;
import courgette.api.CourgetteTestOutput;
import courgette.api.CucumberOptions;
import courgette.api.junit.Courgette;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

//@RunWith(Cucumber.class)
//@CucumberOptions(features = { "src/test/java/resources" }, glue = { "org.mifos.integrationtest.cucumber" }, plugin = {
//        "html:cucumber-report", "json:cucumber.json", "pretty", "html:build/cucumber-report.html", "json:build/cucumber-report.json",
//        "junit:build/cucumber.xml" })

@RunWith(Courgette.class)
@CourgetteOptions(threads = 3, runLevel = CourgetteRunLevel.FEATURE, rerunFailedScenarios = false, testOutput = CourgetteTestOutput.CONSOLE,

        reportTitle = "Paymenthub Test results", reportTargetDir = "build", cucumberOptions = @CucumberOptions(features = "src/test/java/resources", glue = "org.mifos.integrationtest.cucumber", tags = "@gov", publish = true, plugin = {
                "html:cucumber-report", "json:cucumber.json", "pretty", "html:build/cucumber-report.html",
                "json:build/cucumber-report.json", "junit:build/cucumber.xml" }))
@SuppressWarnings({ "FinalClass","HideUtilityClassConstructor" })
public class TestRunner {

    private static WireMockServer wireMockServer;

    @BeforeClass
    public static void setup() {
        // Start WireMock server
        wireMockServer = new WireMockServer(53013);
        wireMockServer.start();
    }

    @AfterClass
    public static void teardown() {
        // Stop WireMock server
        wireMockServer.stop();
    }
}
