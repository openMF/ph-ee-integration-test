package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GsmaConfig {

    @Value("${channel.base-url}")
    public String channelConnectorBaseUrl;
    @Value("${channel.endpoint}")
    public String gsmaEndpoint;
    @Value("${loan.base-url}")
    public String loanBaseUrl;
    @Value("${loan.endpoints.product-endpoint}")
    public String loanProductEndpoint;
    @Value("${loan.endpoints.account-endpoint}")
    public String loanAccountEndpoint;
    @Value("${loan.endpoints.approve-endpoint}")
    public String loanApproveEndpoint;
    @Value("${loan.endpoints.repayment-endpoint}")
    public String loanRepaymentEndpoint;
    @Value("${loan.endpoints.disburse-endpoint}")
    public String loanDisburseEndpoint;
    @Value("${loan.endpoints.accountid-endpoint}")
    public String loanGetAccountIdEndpoint;
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
    @Value("${savings.base-url}")
    public String payerClientBaseUrl;
    @Value("${savings.endpoints.client-endpoint}")
    public String payerClientEndpoint;
    @Value("${amsmifos.mock.base-url}")
    public String amsMifosBasseUrl;
    @Value("${amsmifos.mock.endpoints.deposit-endpoint}")
    public String savingsDepositAccountMockEndpoint;
    @Value("${amsmifos.mock.endpoints.repayment-endpoint}")
    public String loanRepaymentMockEndpoint;
    @Value("${callback_url}")
    public String callbackURL;
    @Value("${amsmifos.status.base-url}")
    public String amsStatusBaseUrl;

    @Value("${amsmifos.status.endpoints}")
    public String amsStatusEndpointUrl;
}