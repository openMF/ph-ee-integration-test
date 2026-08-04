package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GazelleConfig {

    @Value("${gazelle.mifos-api}")
    public String mifosApiUrl;

    @Value("${gazelle.channel-connector}")
    public String channelConnectorUrl;

    @Value("${gazelle.transfer-endpoint}")
    public String transferEndpoint;

    @Value("${gazelle.mifos-auth}")
    public String mifosAuth;

    @Value("${gazelle.payer-tenant}")
    public String payerTenant;

    @Value("${gazelle.payee-tenant}")
    public String payeeTenant;

}
