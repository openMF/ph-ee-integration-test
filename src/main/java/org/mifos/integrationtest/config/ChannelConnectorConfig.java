package org.mifos.integrationtest.config;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ChannelConnectorConfig {

    @Value("${channel-connector.contactpoint}")
    public String channelConnectorContactPoint;

    @Value("${channel-connector.endpoints.transfer}")
    public String transferEndpoint;

    @Value("${channel-connector.endpoints.gsma-p2p}")
    public String gsmaP2PEndpoint;
    @Value("${channel-connector.endpoints.gsma-deposit}")
    public String gsmaP2PDepositEndpoint;
    @Value("${channel-connector.endpoints.collection}")
    public String collectionEndpoint;
    @Value("${channel-connector.endpoints.transferReq}")
    public String transferReqEndpoint;

    public String transferUrl;

    public String requestType;

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    @PostConstruct
    private void setup() {
        transferUrl = channelConnectorContactPoint + transferEndpoint;
    }

}
