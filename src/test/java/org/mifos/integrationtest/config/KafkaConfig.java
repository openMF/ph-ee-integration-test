package org.mifos.integrationtest.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${kafka.brokers}")
    public String kafkaBroker;

    @Value("${kafka.topic}")
    public String kafkaTopic;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() throws UnknownHostException {
        Map<String, Object> map = new HashMap<>();

        map.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker);
        map.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-1");
        map.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        return new DefaultKafkaConsumerFactory<>(map);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListner() throws UnknownHostException {
        ConcurrentKafkaListenerContainerFactory<String, String> containerFactory =
                new ConcurrentKafkaListenerContainerFactory<>();
        containerFactory.setConsumerFactory(consumerFactory());
        return containerFactory;
    }
}