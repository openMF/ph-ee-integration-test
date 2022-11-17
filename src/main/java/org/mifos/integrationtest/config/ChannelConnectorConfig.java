package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ChannelConnectorConfig {

    @Value("${channel-connector.contactpoint}")
    public String channelConnectorContactPoint;

    @Value("${channel-connector.endpoints.transfer}")
    public String transferEndpoint;

    public String transferUrl;

    @PostConstruct
    private void setup() {
        transferUrl = channelConnectorContactPoint + transferEndpoint;
    }

}
