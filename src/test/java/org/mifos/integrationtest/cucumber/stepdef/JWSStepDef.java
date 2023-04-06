package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.en.And;
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

}
