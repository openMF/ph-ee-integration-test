package org.mifos.integrationtest.config;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KongConfig {

    @Value("${kong.service.host}")
    public String serviceHost;

    @Value("${kong.route.host}")
    public String routeHost;

    @Value("${kong.service.channel-connector}")
    public String channelServiceUrl;

    @Value("${kong.route.channel.host}")
    public String channelRouteHost;

    @Value("${kong.route.channel.path}")
    public String channelRoutePath;

    @Value("${kong.admin-contactpoint}")
    public String adminContactPoint;

    @Value("${kong.endpoint.consumers}")
    public String consumerEndpoint;

    @Value("${kong.endpoint.createKey}")
    public String createKeyEndpoint;

    @Value("${kong.endpoint.services}")
    public String servicesEndpoint;

    @Value("${kong.endpoint.createRoute}")
    public String createRouteEndpoint;

    @Value("${kong.endpoint.createPlugin}")
    public String createPluginEndpoint;

    @Value("${kong.endpoint.routes}")
    public String routesEndpoint;

    @Value("${kong.endpoint.plugins}")
    public String pluginsEndpoint;

    @Value("${kong.header.apikey}")
    public String apiKeyHeader;

    public String serviceUrl;

    @PostConstruct
    private void setup() {
        serviceUrl = new StringBuilder().append("https://").append(serviceHost).append("/").toString();
    }

}
