package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MojaloopConfig {

    @Value("${mojaloop.contactpoint}")
    public String mojaloopBaseurl;

    @Value("${mojaloop.endpoint.als}")
    public String addUserToAlsEndpoint;

    @Value("${mojaloop.fspid.payer}")
    public String payerFspId;

    @Value("${mojaloop.fspid.payee}")
    public String payeeFspId;

    @Value("${ml-connector.host}")
    public String mlConnectorHost;

    @Value("${ml-connector.endpoint.get-party}")
    public String mlConnectorGetPartyEndpoint;

    @Value("${ml-connector.endpoint.get-quote}")
    public String mlConnectorGetQuoteEndpoint;

    @Value("${ml-connector.endpoint.transfer}")
    public String mlConnectorTransferEndpoint;
}
