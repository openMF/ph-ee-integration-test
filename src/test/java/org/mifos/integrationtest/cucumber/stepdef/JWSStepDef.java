package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.en.And;
import org.mifos.connector.common.util.CertificateUtil;
import org.mifos.connector.common.util.Constant;
import org.mifos.connector.common.util.SecurityUtil;
import org.mifos.integrationtest.common.Utils;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;

import static com.google.common.truth.Truth.assertThat;

public class JWSStepDef extends BaseStepDef {

    @And("I have clientCorrelationId as {string}")
    public void setClientCorrelationId(String clientCorrelationId) {
        BaseStepDef.clientCorrelationId = clientCorrelationId;
        assertThat(BaseStepDef.clientCorrelationId).isNotEmpty();
    }

    @And("I generate signature")
    public void generateSignature() throws
            IOException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        String fileContent = Files.readString(Paths.get(Utils.getAbsoluteFilePathToResource(BaseStepDef.filename)));
        String jwsDataToBeHashed = new StringBuilder()
                .append(BaseStepDef.clientCorrelationId).append(BaseStepDef.jwsDataSeparator)
                .append(BaseStepDef.tenant).append(BaseStepDef.jwsDataSeparator)
                .append(fileContent).toString();
        String hashedData = SecurityUtil.hash(jwsDataToBeHashed);
        BaseStepDef.signature = SecurityUtil.encryptUsingPrivateKey(hashedData, BaseStepDef.privateKeyString);
        assertThat(BaseStepDef.signature).isNotEmpty();
        logger.info("Generated signature: {}", BaseStepDef.signature);
    }

    @And("The response should have non empty header X-SIGNATURE")
    public void checkNonEmptySignatureKey() {
        assertThat(BaseStepDef.restResponseObject).isNotNull();
        String signatureHeaderValue = BaseStepDef.restResponseObject.getHeader(Constant.HEADER_JWS);
        logger.info("Response signature: {}", signatureHeaderValue);
        assertThat(signatureHeaderValue).isNotEmpty();
        BaseStepDef.signature = signatureHeaderValue;
    }

    @And("The signature should be able successfully validated against certificate {string}")
    public void verifyResponseSignature(String x509Certificate) {
        assertThat(BaseStepDef.restResponseObject).isNotNull();
        String data = BaseStepDef.response;
        String signature = BaseStepDef.restResponseObject.getHeader(Constant.HEADER_JWS);

        Boolean isValidSignature = null;
        try {
            isValidSignature = validateSignature(signature, data, x509Certificate);
        } catch (Exception e) {
            logger.error("Failed step verifyResponseSignature" +
                    " \"The signature should be able successfully validated against certificate {string}\"");
        }
        assertThat(isValidSignature).isNotNull();
        assertThat(isValidSignature).isTrue();
    }

    public boolean validateSignature(String signature, String data, String x509Certificate) throws
            CertificateException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        String publicKey = CertificateUtil.getPublicKey(x509Certificate);
        logger.info("Data to be hashed: {}", data);
        String hashedData = SecurityUtil.hash(data);
        logger.info("Hashed data: {}", hashedData);
        String decodedHash = SecurityUtil.decryptUsingPublicKey(signature, publicKey);
        logger.info("Decoded hash: {}", decodedHash);
        return hashedData.equals(decodedHash);
    }

}
