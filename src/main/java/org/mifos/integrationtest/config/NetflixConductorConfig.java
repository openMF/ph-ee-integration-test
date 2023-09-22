package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NetflixConductorConfig {

    @Value("${netflix-conductor.server.contactpoint}")
    public String conductorServerContactPoint;

    @Value("${netflix-conductor.server.endpoints.home}")
    public String homeEndpoint;

    @Value("${netflix-conductor.server.endpoints.health}")
    public String healthEndpoint;

    @Value("${netflix-conductor.server.endpoints.workflow}")
    public String workflowEndpoint;
}
