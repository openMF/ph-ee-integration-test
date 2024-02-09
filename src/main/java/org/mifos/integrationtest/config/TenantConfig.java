package org.mifos.integrationtest.config;

import java.util.HashMap;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "tenantconfig")
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
