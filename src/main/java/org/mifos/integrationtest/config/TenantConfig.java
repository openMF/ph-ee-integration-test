package org.mifos.integrationtest.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "tenants")
public class TenantConfig {

    private List<TenantProperties> tenant;

    public List<TenantProperties> getTenant() {
        return tenant;
    }

    public void setTenant(List<TenantProperties> tenant) {
        this.tenant = tenant;
    }

    public static class TenantProperties {

        private String name;
        private String value;

        // Getters and setters

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
