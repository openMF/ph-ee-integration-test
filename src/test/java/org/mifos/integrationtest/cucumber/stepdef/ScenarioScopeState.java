package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.spring.ScenarioScope;
import io.restassured.response.Response;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.mifos.connector.common.channel.dto.TransactionChannelRequestDTO;
import org.mifos.integrationtest.common.dto.BatchRequestDTO;
import org.mifos.integrationtest.common.dto.KeycloakTokenResponse;
import org.mifos.integrationtest.common.dto.billpayp2g.BillPaymentsReqDTO;
import org.mifos.integrationtest.common.dto.kong.KeycloakUser;
import org.mifos.integrationtest.common.dto.kong.KongConsumer;
import org.mifos.integrationtest.common.dto.kong.KongConsumerKey;
import org.mifos.integrationtest.common.dto.kong.KongPlugin;
import org.mifos.integrationtest.common.dto.kong.KongRoute;
import org.mifos.integrationtest.common.dto.kong.KongService;
import org.mifos.integrationtest.common.dto.operationsapp.ActuatorResponse;
import org.mifos.integrationtest.common.dto.operationsapp.BatchAndSubBatchSummaryResponse;
import org.mifos.integrationtest.common.dto.operationsapp.BatchDTO;
import org.mifos.integrationtest.common.dto.operationsapp.BatchPaginatedResponse;
import org.mifos.integrationtest.common.dto.operationsapp.BatchTransactionResponse;
import org.mifos.integrationtest.common.dto.operationsapp.PaymentBatchDetail;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class ScenarioScopeState {

    protected String paymentBB2;
    protected String payerIdentifier;
    protected String payeeIdentifier;

    protected String batchId;
    protected String tenant;
    protected String response;
    protected String request;
    protected Integer statusCode;
    protected String accessToken;
    protected String filename;
    protected String requestType;
    protected String clientCorrelationId;
    protected String transactionId;
    protected TransactionChannelRequestDTO inboundTransferMockReq;
    protected String paymentStatusCheckReqDto;
    protected BillPaymentsReqDTO inboundTransferReqP2G;

    protected String billId;
    protected String callbackUrl;
    protected KeycloakTokenResponse keycloakTokenResponse;
    protected String randomData;
    protected String encryptedData;
    protected String decryptedData;
    protected String privateKeyString;
    protected String publicKeyString;
    protected String newPublicKeyString;
    protected PublicKey publicKey;
    protected String certificateString;

    protected String status;
    protected PaymentBatchDetail paymentBatchDetail;
    protected Map<String, Object> batchesEndpointQueryParam = new HashMap<>();
    protected JSONObject requestBody;
    protected BatchRequestDTO batchRequestDTO;
    protected String batchRawRequest;
    protected BatchAndSubBatchSummaryResponse batchAndSubBatchSummaryResponse;
    protected Long currentBalance;
    protected String beneficiaryPayeeIdentity;
    protected KeycloakUser keycloakUser;
    protected String signature;
    protected Response restResponseObject;
    protected String registeringInstituteId;
    protected String programId;
    protected BatchDTO batchDTO;

    protected ActuatorResponse actuatorResponse;
    protected String dateTime;
    public BatchTransactionResponse batchTransactionResponse;
    protected KongConsumer kongConsumer;
    protected KongConsumerKey kongConsumerKey;
    protected KongService kongService;
    protected KongRoute kongRoute;
    protected KongPlugin kongPlugin;
    protected BatchPaginatedResponse batchesResponse;
    protected int gsmaP2PAmtDebit;

    protected int initialBalForPayer;
    protected int initialBalForPayee;

    protected String[] payeeIdentifierforBatch;
    protected int[] initialBalForPayeeForBatch;
    protected int[] gsmaP2PAmtDebitForBatch;

}
