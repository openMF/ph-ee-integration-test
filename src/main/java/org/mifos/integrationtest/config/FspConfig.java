package org.mifos.integrationtest.config;

import java.util.HashMap;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "fspconfig")
public class FspConfig {

    private HashMap<String, String> payeefsp;
    private HashMap<String, String> payerfsp;

    public HashMap<String, String> getPayeefsp() {
        return payeefsp;
    }

    public void setPayeefsp(HashMap<String, String> payeefsp) {
        this.payeefsp = payeefsp;
    }

    public HashMap<String, String> getPayerfsp() {
        return payerfsp;
    }

    public void setPayerfsp(HashMap<String, String> payerfsp) {
        this.payerfsp = payerfsp;
    }

    public String getPayeeFsp(String key) {
        return payeefsp.get(key);
    }

    public String getPayerFsp(String key) {
        return payerfsp.get(key);
    }
}
