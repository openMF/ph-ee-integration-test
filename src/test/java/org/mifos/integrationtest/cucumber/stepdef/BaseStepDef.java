package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import org.mifos.connector.common.channel.dto.TransactionChannelRequestDTO;
import org.mifos.integrationtest.config.BulkProcessorConfig;
import org.mifos.integrationtest.config.ChannelConnectorConfig;
import org.mifos.integrationtest.config.MockServer;
import org.mifos.integrationtest.config.OperationsAppConfig;
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

    @Value("${operations-app.auth.enabled}")
    public Boolean authEnabled;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    protected static String batchId;
    protected static String tenant;
    protected static String response;
    protected static Integer statusCode;
    protected static String accessToken;
    protected static String filename = "ph-ee-bulk-demo-6.csv";
    protected static String requestType;
    protected static String clientCorrelationId;
    protected static String transactionId;
    protected static TransactionChannelRequestDTO inboundTransferMockReq;

}
