package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PayerFundTransferConfig {

    @Value("${payerFundTransfer.tenant.payer}")
    public String payerTenant;

    @Value("${payerFundTransfer.tenant.payee}")
    public String payeeTenant;

    @Value("${savings.base-url}")
    public String clientBaseUrl;
    @Value("${savings.endpoints.client-endpoint}")
    public String clientEndpoint;

    @Value("${savings.base-url}")
    public String savingsBaseUrl;

    @Value("${savings.endpoints.product-endpoint}")
    public String savingsProductEndpoint;
    @Value("${savings.endpoints.approve-endpoint}")
    public String savingsApproveEndpoint;
    @Value("${savings.endpoints.account-endpoint}")
    public String savingsAccountEndpoint;
    @Value("${savings.endpoints.activate-endpoint}")
    public String savingsActivateEndpoint;
    @Value("${savings.endpoints.interop-identifier-endpoint}")
    public String interopIdentifierEndpoint;
    @Value("${savings.endpoints.deposit-endpoint}")
    public String savingsDepositAccountEndpoint;
    @Value("${callback_url}")
    public String callbackURL;
}
