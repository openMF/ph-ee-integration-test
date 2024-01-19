package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KongOidcPluginConfig {

    @Value("${kong.plugin.oidc.scope}")
    public String scope;

    @Value("${kong.plugin.oidc.bearerTokenOnly}")
    public boolean bearerTokenOnly;

}
