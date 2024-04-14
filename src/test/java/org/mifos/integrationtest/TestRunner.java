package org.mifos.integrationtest;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.WireMockServer;
import courgette.api.CourgetteAfterAll;
import courgette.api.CourgetteBeforeAll;
import courgette.api.CourgetteOptions;
import courgette.api.CourgetteRunLevel;
import courgette.api.CourgetteTestOutput;
import courgette.api.CucumberOptions;
import courgette.api.junit.Courgette;
import io.cucumber.java.BeforeAll;
import io.cucumber.junit.Cucumber;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.runner.RunWith;
import org.mifos.integrationtest.config.MockServer;
import org.mifos.integrationtest.config.MockServerConfig;
import org.mifos.integrationtest.config.WireMockServerSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

//@RunWith(Cucumber.class)
//@CucumberOptions(features = { "src/test/java/resources" }, glue = { "org.mifos.integrationtest.cucumber" }, plugin = {
//        "html:cucumber-report", "json:cucumber.json", "pretty", "html:build/cucumber-report.html", "json:build/cucumber-report.json",
//        "junit:build/cucumber.xml" })

@RunWith(Courgette.class)
@CourgetteOptions(threads = 3, runLevel = CourgetteRunLevel.FEATURE, rerunFailedScenarios = false, testOutput = CourgetteTestOutput.CONSOLE, generateCourgetteRunLog = true ,

        reportTitle = "Paymenthub Test results", reportTargetDir = "build", cucumberOptions = @CucumberOptions(features = "src/test/java/resources", glue = "org.mifos.integrationtest.cucumber", tags = "@gov", publish = true, plugin = {
                "html:cucumber-report", "json:cucumber.json", "pretty", "html:build/cucumber-report.html",
                "json:build/cucumber-report.json", "junit:build/cucumber.xml" }))
@SuppressWarnings({ "FinalClass", "HideUtilityClassConstructor" })
public class TestRunner {

    @CourgetteBeforeAll
    public static void setupWireMockServer() {
        WireMockServerSingleton.getInstance(); // Start WireMock server
    }

    @CourgetteAfterAll
    public static void stopWireMockServer() {
        WireMockServerSingleton.getInstance().stop(); // Stop WireMock server
    }
}
