package org.mifos.integrationtest.cucumber.stepdef;

import static com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents;
import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import java.util.Objects;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.EmailRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

public class EmailStepDef extends BaseStepDef {

    @Value("${callback_url}")
    private String callbackURL;

    @Value("${messageGateway.contactpoint}")
    private String messageGatewayHost;

    @Value("${messageGateway.endpoint.email}")
    private String mgMail;
    @Value("${spring.mail.host}")
    private String smtpHost;

    @Value("${spring.mail.port}")
    private int smtpPort;

    @Autowired
    private Environment env;

    private GreenMail greenMail;

    @Given("the email service is running")
    public void theEmailServiceIsRunning() {
        int port = Integer.parseInt(Objects.requireNonNull(env.getProperty("spring.mail.port")));
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.setUser("greenmail", "greenmail");
        greenMail.start();

    }

    @When("I send an email to the following recipients with subject {string} and body {string} with callbackurl as {string} and get {int}")
    public void iSendAnEmailToWithSubjectAndBody(String subject, String body, String url, int expectedStatus, DataTable dataTable)
            throws JsonProcessingException {
        List<String> to = dataTable.asList(String.class);
        EmailRequestDTO emailRequest = new EmailRequestDTO(to, subject, body);

        // Convert the payload to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(emailRequest);

        // Get the default request specification
        RequestSpecification requestSpec = Utils.getDefaultSpec();

        // Send the POST request and get the response
        scenarioScopeState.response = RestAssured.given(requestSpec).header("Content-Type", "application/json")
                .header("X-CallbackUrl", callbackURL + url).header("Correlation-Id", scenarioScopeState.clientCorrelationId)
                .header("Platform-Tenant-Id", scenarioScopeState.tenant).baseUri(messageGatewayHost).body(jsonPayload).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when().post(mgMail).andReturn().asString();

        logger.info("Mail Response {}", scenarioScopeState.response);

    }

    @Then("the email should be sent to all recipients with subject {string} and body {string}")
    public void theEmailShouldBeSentToWithSubjectAndBody(String subject, String body) throws Exception {
        await().atMost(awaitMost, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            logger.info(String.valueOf(greenMail.getReceivedMessages().length));
            assertThat(greenMail.getReceivedMessages().length == 1).isTrue();

            String receivedTo = GreenMailUtil.getAddressList(greenMail.getReceivedMessages()[0].getAllRecipients());
            String receivedSubject = greenMail.getReceivedMessages()[0].getSubject();
            String receivedBody = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]);

            assertThat(receivedTo).isNotNull();
            assertThat(subject.equals(receivedSubject)).isTrue();
            assertThat(body.equals(receivedBody)).isTrue();
        });
    }

    @Then("I should be able to extract error from response")
    public void iShouldBeAbleToExtractErrorFromResponse() {
        assertThat(scenarioScopeState.response).isNotNull();
        assertThat(scenarioScopeState.response).containsMatch("Bad Request");
    }

    @And("I can verify callback received with success")
    public void iCanVerifyCallbackReceivedWithSuccess() {
        await().atMost(awaitMost, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            boolean flag = false;
            List<ServeEvent> allServeEvents = getAllServeEvents();
            for (int i = allServeEvents.size() - 1; i >= 0; i--) {
                ServeEvent request = allServeEvents.get(i);
                if (!(request.getRequest().getBodyAsString()).isEmpty() && request.getRequest().getUrl().equals("/sendMail")) {
                    String rootNode = null;
                    rootNode = request.getRequest().getBodyAsString();
                    logger.info("Rootnode value:" + rootNode);
                    assertThat(rootNode.contains("Email sent successfully")).isTrue();
                }
            }

        });
    }

    @And("I can verify callback received with failure")
    public void iCanVerifyCallbackReceivedWithFailure() {
        await().atMost(awaitMost, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            boolean flag = false;
            List<ServeEvent> allServeEvents = getAllServeEvents();
            for (int i = allServeEvents.size() - 1; i >= 0; i--) {
                ServeEvent request = allServeEvents.get(i);
                if (!(request.getRequest().getBodyAsString()).isEmpty() && request.getRequest().getUrl().equals("/sendMail")) {
                    String rootNode = null;
                    rootNode = request.getRequest().getBodyAsString();
                    logger.info("Rootnode value:" + rootNode);
                    assertThat(rootNode
                            .contains("Email could not be sent to [recipient1@example.com] because of Mail server connection failed"))
                            .isTrue();
                }
            }

        });
    }

    @Then("I should be able to stop the greenmail mock")
    public void iShouldBeAbleToStopTheGreenmailMock() {
        if (greenMail != null) {
            greenMail.stop();
        }
    }
}
