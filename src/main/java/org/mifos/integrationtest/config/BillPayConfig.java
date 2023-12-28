package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BillPayConfig {
    @Value("${bill_pay.contactpoint}")
    public String billPayContactPoint;
    @Value("${bill_pay.endpoint.billerRtpRequest}")
    public String billerRtpEndpoint;
}
