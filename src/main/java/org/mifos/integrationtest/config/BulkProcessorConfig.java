package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BulkProcessorConfig {

    @Value("${bulk-processor.contactpoint}")
    public String bulkProcessorContactPoint;

    @Value("${bulk-processor.endpoints.bulk-transactions}")
    public String bulkTransactionEndpoint;

    public String bulkTransactionUrl;

    public String callbackUrl;

   public int retryCount;

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    @PostConstruct
    private void setup() {
        bulkTransactionUrl = bulkProcessorContactPoint + bulkTransactionEndpoint;
    }


    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
}
