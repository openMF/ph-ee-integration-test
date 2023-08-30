package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MockPaymentSchemaConfig {

    @Value("${mock-payment-schema.contactpoint}")
    public String mockPaymentSchemaContactPoint;

    @Value("${mock-payment-schema.endpoints.mock-batch-authorization}")
    public String mockBatchAuthorizationEndpoint;
}
