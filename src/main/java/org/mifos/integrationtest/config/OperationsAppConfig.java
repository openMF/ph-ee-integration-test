package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class OperationsAppConfig {

    @Value("${operations-app.contactpoint}")
    public String operationAppContactPoint;

    @Value("${operations-app.endpoints.batch-transaction}")
    public String batchTransactionEndpoint;

    @Value("${operations-app.endpoints.batch-summary}")
    public String batchSummaryEndpoint;

    @Value("${operations-app.endpoints.batch-details}")
    public String batchDetailsEndpoint;

    @Value("${operations-app.endpoints.auth}")
    public String authEndpoint;

    @Value("${operations-app.endpoints.transfers}")
    public String transfersEndpoint;

    @Value("${operations-app.endpoints.transactionRequests}")
    public String transactionRequestsEndpoint;

    @Value("${operations-app.endpoints.batch-aggregation}")
    public String batchAggregationEndpoint;

    @Value("${operations-app.username}")
    public String username;

    @Value("${operations-app.password}")
    public String password;

    public String batchTransactionUrl;

    public String batchSummaryUrl;

    public String batchDetailsUrl;

    public String transfersUrl;

    public String transactionRequestsUrl;

    public String authUrl;

    @PostConstruct
    private void setup() {
        batchTransactionUrl = operationAppContactPoint + batchTransactionEndpoint;
        batchSummaryUrl = operationAppContactPoint + batchSummaryEndpoint;
        batchDetailsUrl = operationAppContactPoint + batchDetailsEndpoint;
        authUrl = operationAppContactPoint + authEndpoint;
        transfersUrl = operationAppContactPoint + transfersEndpoint;
        transactionRequestsUrl = operationAppContactPoint + transactionRequestsEndpoint;
    }

}
