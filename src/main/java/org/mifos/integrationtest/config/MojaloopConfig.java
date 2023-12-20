package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MojaloopConfig {

    @Value("${mojaloop.contactpoint}")
    public String mojaloopBaseurl;

    @Value("${mojaloop.central-ledger-contactpoint}")
    public String mojaloopCentralLedgerBaseurl;

    @Value("${mojaloop.account-lookup-admin-contactpoint}")
    public String mojaloopAccountLookupAdminBaseurl;

    @Value("${mojaloop.endpoint.als}")
    public String addUserToAlsEndpoint;

    @Value("${mojaloop.endpoint.hub-account}")
    public String mojaloopHubAccount;

    @Value("${mojaloop.endpoint.settlement-model}")
    public String settlementModel;

    @Value("${mojaloop.endpoint.participant}")
    public String participant;

    @Value("${mojaloop.endpoint.position-and-limits}")
    public String initialPositionAndLimitEndpoint;

    @Value("${mojaloop.endpoint.add-callback}")
    public String addCallbackEndpoint;

    @Value("${mojaloop.endpoint.record-fund}")
    public String recordFundsEndpoint;

    @Value("${mojaloop.endpoint.oracle}")
    public String oracleEndpoint;

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
