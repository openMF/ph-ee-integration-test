package org.mifos.integrationtest.config;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class PaymentStatusCheckConfig {

    public List<String> requestIds = new ArrayList<>();

    @PostConstruct
    public void setup() {

    }
}
