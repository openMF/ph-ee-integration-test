package org.mifos.integrationtest.config;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OperationsAppConfig {

    @Value("${operations-app.contactpoint}")
    public String operationAppContactPoint;

    @Value("${operations-app.dpgcontactpoint}")
    public String dpgOperationAppContactPoint;

    @Value("${operations-app.endpoints.batch-transaction}")
    public String batchTransactionEndpoint;

    @Value("${operations-app.endpoints.batch-summary}")
    public String batchSummaryEndpoint;

    @Value("${operations-app.endpoints.batch-details}")
    public String batchDetailsEndpoint;

    @Value("${operations-app.endpoints.batch-aggregate}")
    public String batchAggregateEndpoint;

    @Value("${operations-app.endpoints.auth}")
    public String authEndpoint;

    @Value("${operations-app.endpoints.transfers}")
    public String transfersEndpoint;

    @Value("${operations-app.endpoints.variables}")
    public String variablesEndpoint;

    @Value("${operations-app.endpoints.transactionRequests}")
    public String transactionRequestsEndpoint;

    @Value("${operations-app.endpoints.batches}")
    public String batchesEndpoint;

    @Value("${operations-app.username}")
    public String username;

    @Value("${operations-app.password}")
    public String password;

    public String batchTransactionUrl;

    public String batchSummaryUrl;

    public String batchDetailsUrl;

    public String batchAggregateUrl;

    public String transfersUrl;

    public String transactionRequestsUrl;

    public String batchesUrl;

    public String authUrl;

    @Value("${operations-app.endpoints.reportEndpoint}")
    public String reportEndpoint;

    @Value("${operations-app.endpoints.reportCreate}")
    public String reportCreate;

    @Value("${operations-app.endpoints.reportUpdate}")
    public String reportUpdate;

    @Value("${operations-app.endpoints.reportSingleReport}")
    public String reportSingleReport;

    @PostConstruct
    private void setup() {
        batchTransactionUrl = operationAppContactPoint + batchTransactionEndpoint;
        batchSummaryUrl = operationAppContactPoint + batchSummaryEndpoint;
        batchDetailsUrl = operationAppContactPoint + batchDetailsEndpoint;
        batchAggregateUrl = operationAppContactPoint + batchAggregateEndpoint;
        authUrl = operationAppContactPoint + authEndpoint;
        transfersUrl = operationAppContactPoint + transfersEndpoint;
        transactionRequestsUrl = operationAppContactPoint + transactionRequestsEndpoint;
        batchesUrl = operationAppContactPoint + batchesEndpoint;
    }

}
