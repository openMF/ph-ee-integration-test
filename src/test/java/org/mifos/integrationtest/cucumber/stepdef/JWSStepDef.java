package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import io.cucumber.java.en.And;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.mifos.connector.common.util.CertificateUtil;
import org.mifos.connector.common.util.Constant;
import org.mifos.connector.common.util.SecurityUtil;
import org.mifos.integrationtest.common.UniqueNumberGenerator;
import org.mifos.integrationtest.config.JWSKeyConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class JWSStepDef extends BaseStepDef {

    @Autowired
    JWSKeyConfig jwsKeyConfig;

    @And("I generate clientCorrelationId")
    public void setClientCorrelationId() {
        scenarioScopeState.clientCorrelationId = UniqueNumberGenerator.generateUniqueNumber(12);
        assertThat(scenarioScopeState.clientCorrelationId).isNotEmpty();
    }

    @And("I generate signature")
    public void generateSignatureStep() throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        if (scenarioScopeState.filename != null) {
            scenarioScopeState.signature = generateSignature(scenarioScopeState.clientCorrelationId, scenarioScopeState.tenant,
                    scenarioScopeState.filename, true);
        } else {
            scenarioScopeState.signature = generateSignature(scenarioScopeState.clientCorrelationId, scenarioScopeState.tenant,
                    scenarioScopeState.batchRawRequest, false);
        }
        assertThat(scenarioScopeState.signature).isNotEmpty();
        logger.info("Generated signature: {}", scenarioScopeState.signature);
    }

    @And("The response should have non empty header X-SIGNATURE")
    public void checkNonEmptySignatureKey() {
        assertThat(scenarioScopeState.restResponseObject).isNotNull();
        String signatureHeaderValue = scenarioScopeState.restResponseObject.getHeader(Constant.HEADER_JWS);
        logger.info("Response signature: {}", signatureHeaderValue);
        assertThat(signatureHeaderValue).isNotEmpty();
        scenarioScopeState.signature = signatureHeaderValue;
    }

    @And("The signature should be able successfully validated against certificate")
    public void verifyResponseSignature() {
        assertThat(scenarioScopeState.restResponseObject).isNotNull();
        assertThat(jwsKeyConfig).isNotNull();
        String data = scenarioScopeState.response;
        String signature = scenarioScopeState.restResponseObject.getHeader(Constant.HEADER_JWS);

        Boolean isValidSignature = null;
        try {
            isValidSignature = validateSignature(signature, data, jwsKeyConfig.x509Certificate);
        } catch (Exception e) {
            logger.error("Failed step verifyResponseSignature"
                    + " \"The signature should be able successfully validated against certificate {string}\"");
        }
        assertThat(isValidSignature).isNotNull();
        assertThat(isValidSignature).isTrue();
    }

    public boolean validateSignature(String signature, String data, String x509Certificate)
            throws CertificateException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
            InvalidKeySpecException, InvalidKeyException {
        String publicKey = CertificateUtil.getPublicKey(x509Certificate);
        logger.info("Data to be hashed: {}", data);
        String hashedData = SecurityUtil.hash(data);
        logger.info("Hashed data: {}", hashedData);
        String decodedHash = SecurityUtil.decryptUsingPublicKey(signature, publicKey);
        logger.info("Decoded hash: {}", decodedHash);
        return hashedData.equals(decodedHash);
    }

}
