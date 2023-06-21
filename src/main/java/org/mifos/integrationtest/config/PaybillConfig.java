package org.mifos.integrationtest.config;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaybillConfig {

    @Value("${paybill.mpesa-connector.contactpoint}")
    public String mpesaContactPoint;
    @Value("${paybill.mpesa-connector.endpoints.validation}")
    public String mpesaValidateEndpoint;
    @Value("${paybill.mpesa-connector.endpoints.settlement}")
    public String mpesaSettlementEndpoint;
    public String mpesaValidateUrl;
    public String mpesaSettlementUrl;

    @PostConstruct
    public void setup() {
        mpesaValidateUrl = mpesaContactPoint + mpesaValidateEndpoint;
        mpesaSettlementUrl = mpesaContactPoint + mpesaSettlementEndpoint;
    }
}
