package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import org.apache.commons.lang3.StringUtils;
import org.mifos.connector.common.channel.dto.TransactionChannelRequestDTO;
import org.mifos.connector.common.util.SecurityUtil;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.operationsapp.BatchDTO;
import org.mifos.integrationtest.common.dto.operationsapp.BatchTransactionResponse;
import org.mifos.integrationtest.config.BulkProcessorConfig;
import org.mifos.integrationtest.config.ChannelConnectorConfig;
import org.mifos.integrationtest.config.IdentityMapperConfig;
import org.mifos.integrationtest.config.MockServer;
import org.mifos.integrationtest.config.OperationsAppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

// this class is the base for all the cucumber step definitions
public class BaseStepDef {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    OperationsAppConfig operationsAppConfig;

    @Autowired
    BulkProcessorConfig bulkProcessorConfig;

    @Autowired
    ChannelConnectorConfig channelConnectorConfig;

    @Autowired
    MockServer mockServer;

    @Autowired
    IdentityMapperConfig identityMapperConfig;

    @Value("${operations-app.auth.enabled}")
    public Boolean authEnabled;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    protected static String batchId;
    protected static String tenant;
    protected static String response;
    protected static String request;
    protected static Integer statusCode;
    protected static String accessToken;
    protected static String filename;
    protected static String defaultFileName = "ph-ee-bulk-demo-6.csv";
    protected static String requestType;
    protected static String clientCorrelationId;
    protected static String transactionId;
    protected static TransactionChannelRequestDTO inboundTransferMockReq;
    protected static String callbackUrl;

    protected static String randomData;
    protected static String encryptedData;
    protected static String decryptedData;
    protected static String privateKeyString;
    protected static String publicKeyString;
    protected static String newPublicKeyString;
    protected static PublicKey publicKey;
    protected static String certificateString;
    protected static X509Certificate x509Certificate;
    protected static String jwsDataSeparator = ":";
    protected static String signature;
    protected static Response restResponseObject;
    protected static String registeringInstituteId;
    public static String programId;
    public static BatchDTO batchDTO;
    public static BatchTransactionResponse batchTransactionResponse;

    protected static int throttleTime;

    protected static int subBatchSize;

    // if data passed as a filename/absoluteFilePath then pass isDataAFile as true or else false
    protected String generateSignature(String clientCorrelationId, String tenant, String data, boolean isDataAFile) throws IOException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        StringBuilder jwsDataToBeHashedBuilder = new StringBuilder()
                .append(clientCorrelationId).append(BaseStepDef.jwsDataSeparator)
                .append(tenant);
        if (isDataAFile) {
            if (StringUtils.isNotBlank(filename)) {
                String fileContent = Files.readString(Paths.get(Utils.getAbsoluteFilePathToResource(BaseStepDef.filename)));
                jwsDataToBeHashedBuilder.append(BaseStepDef.jwsDataSeparator).append(fileContent);
            }
        } else {
            if (StringUtils.isNotBlank(data)) {
                jwsDataToBeHashedBuilder.append(BaseStepDef.jwsDataSeparator).append(data);
            }
        }
        String jwsDataToBeHashed = jwsDataToBeHashedBuilder.toString();
        logger.info("Data to be hashed: {}", jwsDataToBeHashed);
        String hashedData = SecurityUtil.hash(jwsDataToBeHashed);
        return SecurityUtil.encryptUsingPrivateKey(hashedData, BaseStepDef.privateKeyString);
    }

}
