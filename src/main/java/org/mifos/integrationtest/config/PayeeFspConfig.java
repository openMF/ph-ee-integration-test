package org.mifos.integrationtest.config;

import java.util.HashMap;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "payeefspconfig")
public class PayeeFspConfig {

    public void setFsps(HashMap<String, String> fsps) {
        this.fsps = fsps;
    }

    public HashMap<String, String> getFsps() {
        return fsps;
    }

    public String getFsp(String key) {
        return fsps.get(key);
    }

    private HashMap<String, String> fsps;
}
