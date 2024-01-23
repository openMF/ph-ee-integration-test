package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.mifos.connector.common.util.CertificateUtil;

public class CertificateStepDef extends BaseStepDef {

    @Given("I have X509 certificate {string}")
    public void setX509CertificateString(String certificate) {
        scenarioScopeDef.certificateString = certificate;
    }

    @When("I have null certificate")
    public void assertNullCertificate() {
        assertThat(scenarioScopeDef.certificateString).isNotEmpty();
    }

    @Then("I should be able to parse the certificate using CertificateUtils")
    public void assertParseCertificate() throws CertificateException {
        X509Certificate certificate = parseCertificate();
        assertThat(certificate).isNotNull();
    }

    @And("I can create certificate object")
    public void notNullParseCertificate() throws CertificateException {
        BaseStepDef.x509Certificate = parseCertificate();
    }

    @When("I parse the public key")
    public void fetchPublicKeyFromX509Certificate() {
        scenarioScopeDef.publicKey = CertificateUtil.parseRSAPublicKey(BaseStepDef.x509Certificate);
    }

    @Then("Public key should be non empty")
    public void publicKeyNotEmptyCheck() {
        assertThat(scenarioScopeDef.publicKey).isNotNull();
    }

    public X509Certificate parseCertificate() throws CertificateException {
        return CertificateUtil.parseX509Certificate(scenarioScopeDef.certificateString);
    }

}
