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
    @Value("${savings.endpoints.deposit-endpoint}")
    public String savingsDepositAccountEndpoint;
    @Value("${savings.base-url}")
    public String payerClientBaseUrl;
    @Value("${savings.endpoints.client-endpoint}")
    public String payerClientEndpoint;
}
