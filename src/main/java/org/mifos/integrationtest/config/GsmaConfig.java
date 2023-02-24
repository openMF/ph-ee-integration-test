package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GsmaConfig {
    @Value("${channel.base-url}")
    public static String channelConnectorBaseUrl;
    @Value("${channel.endpoint}")
    public static String gsmaEndpoint;
    @Value("${loan.base-url}")
    public static String loanBaseUrl;
    @Value("${loan.endpoints.product-endpoint}")
    public static String loanProductEndpoint;
    @Value("${loan.endpoints.account-endpoint}")
    public static String loanAccountEndpoint;
    @Value("${loan.endpoints.approve-endpoint}")
    public static String loanApproveEndpoint;
    @Value("${loan.endpoints.repayment-endpoint}")
    public static String loanRepaymentEndpoint;
    @Value("${savings.base-url}")
    public static String savingsBaseUrl;
    @Value("${savings.endpoints.product-endpoint}")
    public static String savingsProductEndpoint;
    @Value("${savings.endpoints.approve-endpoint}")
    public static String savingsApproveEndpoint;
    @Value("${savings.endpoints.account-endpoint}")
    public static String savingsAccountEndpoint;
    @Value("${savings.endpoints.activate-endpoint}")
    public static String savingsActivateEndpoint;
    @Value("${savings.endpoints.deposit-endpoint}")
    public static String savingsDepositAccountEndpoint;
    @Value("${savings.base-url}")
    public static String payerClientBaseUrl;
    @Value("${savings.endpoints.client-endpoint}")
    public static String payerClientEndpoint;
}
