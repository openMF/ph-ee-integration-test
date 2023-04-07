package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaConfig {
    @Value("${kafka.brokers}")
    public String kafkaBroker;

    @Value("${kafka.topic}")
    public String kafkaTopic;
}