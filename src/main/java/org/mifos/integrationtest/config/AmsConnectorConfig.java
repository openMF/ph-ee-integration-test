package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.*;

import javax.annotation.PostConstruct;

@Component
public class AmsConnectorConfig {

    @Value("${mifos-connector.endpoints.account_status}")
    public String accountStatusEndpoint;

    @Value("${mifos-connector.contactpoint}")
    public String amsConnectorContactPoint;

    public String accountStatusUrl;

    @PostConstruct
    private void setup() {
        accountStatusUrl = amsConnectorContactPoint + accountStatusEndpoint;
    }


}