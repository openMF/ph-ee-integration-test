package org.mifos.integrationtest.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mojaloop")
public class MojaloopCallbackEndpoints {

    private List<CallbackEndpoint> callbackEndpoints;

    public List<CallbackEndpoint> getCallbackEndpoints() {
        return callbackEndpoints;
    }

    public void setCallbackEndpoints(List<CallbackEndpoint> callbackEndpoints) {
        this.callbackEndpoints = callbackEndpoints;
    }

    public static class CallbackEndpoint {

        private String type;
        private String value;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
