package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mifos.connector.common.util.CertificateUtil;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static com.google.common.truth.Truth.assertThat;

public class CertificateStepDef extends BaseStepDef {

    @Given("I have X509 certificate {string}")
    public void setX509CertificateString(String certificate) {
        BaseStepDef.certificateString = certificate;
    }

    @When("I have null certificate")
    public void assertNullCertificate() {
        assertThat(BaseStepDef.certificateString).isNotEmpty();
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
        BaseStepDef.publicKey = CertificateUtil.parseRSAPublicKey(BaseStepDef.x509Certificate);
    }

    @Then("Public key should be non empty")
    public void publicKeyNotEmptyCheck() {
        assertThat(BaseStepDef.publicKey).isNotNull();
    }

    public X509Certificate parseCertificate() throws CertificateException {
        return CertificateUtil.parseX509Certificate(BaseStepDef.certificateString);
    }

}
