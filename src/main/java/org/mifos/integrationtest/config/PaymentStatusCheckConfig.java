package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class PaymentStatusCheckConfig {


    public List<String> requestIds = new ArrayList<>();

    @PostConstruct
    public void setup() {

    }
}
