package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.mifos.connector.common.util.JsonWebSignature;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.config.BulkProcessorConfig;
import org.mifos.integrationtest.config.ChannelConnectorConfig;
import org.mifos.integrationtest.config.IdentityMapperConfig;
import org.mifos.integrationtest.config.MockServer;
import org.mifos.integrationtest.config.OperationsAppConfig;
import org.mifos.integrationtest.config.VoucherManagementConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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

    @Autowired
    VoucherManagementConfig voucherManagementConfig;

    @Value("${operations-app.auth.enabled}")
    public Boolean authEnabled;

    @Autowired
    ScenarioScopeDef scenarioScopeDef;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    // protected static String batchId;
    // protected static String tenant;
    // protected static String response;
    // protected static String request;
    // protected static Integer statusCode;
    // protected static String accessToken;
    // protected static String filename;
    protected static String defaultFileName = "ph-ee-bulk-demo-6.csv";
    // protected static String requestType;
    // protected static String clientCorrelationId;
    // protected static String transactionId;
    // protected static TransactionChannelRequestDTO inboundTransferMockReq;
    // protected static String paymentStatusCheckReqDto;
    // protected static BillPaymentsReqDTO inboundTransferReqP2G;
    // protected static String callbackUrl;
    // protected static KeycloakTokenResponse keycloakTokenResponse;
    // protected static String randomData;
    // protected static String encryptedData;
    // protected static String decryptedData;
    // protected static String privateKeyString;
    // protected static String publicKeyString;
    // protected static String newPublicKeyString;
    // protected static PublicKey publicKey;
    // protected static String certificateString;
    //
    // protected static String status;
    protected static X509Certificate x509Certificate;
    protected static String jwsDataSeparator = ":";
    // protected static String signature;
    // protected static Response restResponseObject;
    // protected static String registeringInstituteId;
    // protected static String programId;
    // protected static BatchDTO batchDTO;
    //
    // protected static ActuatorResponse actuatorResponse;
    // protected static String dateTime;
    // public static BatchTransactionResponse batchTransactionResponse;
    // protected static KongConsumer kongConsumer;
    // protected static KongConsumerKey kongConsumerKey;
    // protected static KongService kongService;
    // protected static KongRoute kongRoute;
    // protected static KongPlugin kongPlugin;
    // protected static BatchPaginatedResponse batchesResponse;
    protected static String dateFormat = "yyyy-MM-dd HH:mm:ss";
    // protected static Map<String, Object> batchesEndpointQueryParam = new HashMap<>();
    // protected static JSONObject requestBody;
    // protected static BatchRequestDTO batchRequestDTO;
    // protected static String batchRawRequest;
    // protected static BatchAndSubBatchSummaryResponse batchAndSubBatchSummaryResponse;
    // protected static Long currentBalance;
    // protected static String beneficiaryPayeeIdentity;
    // protected static String payer_identifier;
    // protected static KeycloakUser keycloakUser;
    protected static String keycloakCurrentUserPassword = "password";
    // protected static PaymentBatchDetail paymentBatchDetail;

    // protected static int gsmaP2PAmtDebit;

    protected static String getCurrentDateInFormat() {
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        // Get the current time in the specified time zone
        ZonedDateTime currentTimeInZone = ZonedDateTime.now(zoneId);
        // Define a format for the output
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return currentTimeInZone.format(formatter);
    }

    protected static String billId;

    // if data passed as a filename/absoluteFilePath then pass isDataAFile as true or else false
    protected String generateSignature(String clientCorrelationId, String tenant, String data, boolean isDataAFile)
            throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
            InvalidKeySpecException, InvalidKeyException {

        JsonWebSignature jsonWebSignature = new JsonWebSignature.JsonWebSignatureBuilder().setClientCorrelationId(clientCorrelationId)
                .setTenantId(tenant).setIsDataAsFile(isDataAFile)
                .setData(isDataAFile ? Utils.getAbsoluteFilePathToResource(scenarioScopeDef.filename) : data).build();

        return jsonWebSignature.getSignature(scenarioScopeDef.privateKeyString);
    }

    private <T> void assertNonEmptyArray(List<T> objects) {
        assertNotNull(objects);
        assertThat(objects).isNotEmpty();
        assertThat(objects.size()).isGreaterThan(0);
    }

    protected <T> void assertNotNull(T object) {
        assertThat(object).isNotNull();
    }

}
