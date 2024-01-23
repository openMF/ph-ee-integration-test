package org.mifos.integrationtest.cucumber.stepdef;

import org.mifos.connector.common.channel.dto.TransactionChannelRequestDTO;
import org.mifos.integrationtest.common.dto.KeycloakTokenResponse;
import org.mifos.integrationtest.common.dto.billpayp2g.BillPaymentsReqDTO;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

@Component
public class ScenarioScopeDef {

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

}
