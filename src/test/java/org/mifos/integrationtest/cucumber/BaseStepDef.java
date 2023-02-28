package org.mifos.integrationtest.cucumber;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import org.mifos.integrationtest.config.BulkProcessorConfig;
import org.mifos.integrationtest.config.ChannelConnectorConfig;
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

    @Value("${operations-app.auth.enabled}")
    public Boolean authEnabled;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    protected static String batchId;
    protected static String tenant;
    protected static String response;
    protected static String accessToken;
    protected static String filename;
    protected static String requestType;

}
