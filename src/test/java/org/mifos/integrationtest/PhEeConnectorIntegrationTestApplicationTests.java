package org.mifos.integrationtest;

import io.cucumber.junit.CucumberOptions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@CucumberOptions(features = "src/test/java/resources/zeebe.feature")
class PhEeConnectorIntegrationTestApplicationTests {

	@Test
	void contextLoads() {
	}

}
