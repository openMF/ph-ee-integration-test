package org.mifos.integrationtest.config;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    @Value("${billPay.endpoints.billerRtpRequest}")
    public String billerRtpEndpoint;

    @Value("${billPay.endpoints.billStatus}")
    public String statusEndpoint;

    @PostConstruct
    private void setup() {
        inquiryEndpoint = billPayContactPoint + inquiryEndpoint;
    }

}
