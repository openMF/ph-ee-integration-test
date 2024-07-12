package org.mifos.integrationtest.config;

import java.util.HashMap;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "tenantconfig")
@Component
public class TenantConfig {

    public void setTenants(HashMap<String, String> tenants) {
        this.tenants = tenants;
    }

    public HashMap<String, String> getTenants() {
        return tenants;
    }

    public String getTenant(String key) {
        return tenants.get(key);
    }

    private HashMap<String, String> tenants;
}
