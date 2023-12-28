package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BillPayConnectorConfig {

    @Value("${billPay.contactpoint}")
    public String billPayContactPoint;

    @Value("${billPay.endpoints.inquiry}")
    public String inquiryEndpoint;

    @Value("${billPay.endpoints.payments}")
    public String paymentsEndpoint;

    @Value("${callback_url}")
    public String callbackURL;

    @PostConstruct
    private void setup() {
        inquiryEndpoint = billPayContactPoint + inquiryEndpoint;
    }

}
