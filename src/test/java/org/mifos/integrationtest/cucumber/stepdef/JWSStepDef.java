package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import io.cucumber.java.en.And;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.mifos.connector.common.util.CertificateUtil;
import org.mifos.connector.common.util.Constant;
import org.mifos.connector.common.util.SecurityUtil;
import org.mifos.integrationtest.config.JWSKeyConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class JWSStepDef extends BaseStepDef {

    @Autowired
    JWSKeyConfig jwsKeyConfig;

    @And("I generate clientCorrelationId")
    public void setClientCorrelationId() {
        scenarioScopeDef.clientCorrelationId = UUID.randomUUID().toString();
        assertThat(scenarioScopeDef.clientCorrelationId).isNotEmpty();
    }

    @And("I generate signature")
    public void generateSignatureStep() throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        if (scenarioScopeDef.filename != null) {
            scenarioScopeDef.signature = generateSignature(scenarioScopeDef.clientCorrelationId, scenarioScopeDef.tenant,
                    scenarioScopeDef.filename, true);
        } else {
            scenarioScopeDef.signature = generateSignature(scenarioScopeDef.clientCorrelationId, scenarioScopeDef.tenant,
                    scenarioScopeDef.batchRawRequest, false);
        }
        assertThat(scenarioScopeDef.signature).isNotEmpty();
        logger.info("Generated signature: {}", scenarioScopeDef.signature);
    }

    @And("The response should have non empty header X-SIGNATURE")
    public void checkNonEmptySignatureKey() {
        assertThat(scenarioScopeDef.restResponseObject).isNotNull();
        String signatureHeaderValue = scenarioScopeDef.restResponseObject.getHeader(Constant.HEADER_JWS);
        logger.info("Response signature: {}", signatureHeaderValue);
        assertThat(signatureHeaderValue).isNotEmpty();
        scenarioScopeDef.signature = signatureHeaderValue;
    }

    @And("The signature should be able successfully validated against certificate")
    public void verifyResponseSignature() {
        assertThat(scenarioScopeDef.restResponseObject).isNotNull();
        assertThat(jwsKeyConfig).isNotNull();
        String data = scenarioScopeDef.response;
        String signature = scenarioScopeDef.restResponseObject.getHeader(Constant.HEADER_JWS);

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
