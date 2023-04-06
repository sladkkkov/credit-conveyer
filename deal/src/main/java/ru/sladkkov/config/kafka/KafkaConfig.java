package ru.sladkkov.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        // get configs on application.properties/yml
        Map<String, Object> properties = kafkaProperties.buildProducerProperties();
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic topicFinishReqistration() {
        return TopicBuilder
                .name("finish-registration")
                .partitions(2)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topicCreateDocuments() {
        return TopicBuilder
                .name("create-documents")
                .partitions(2)
                .replicas(1)
                .build();
    }


    @Bean
    public NewTopic topicSendDocuments() {
        return TopicBuilder
                .name("send-documents")
                .partitions(2)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topicSendSes() {
        return TopicBuilder
                .name("send-ses")
                .partitions(2)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topicCreditIssued() {
        return TopicBuilder
                .name("credit-issued")
                .partitions(2)
                .replicas(1)
                .build();
    }    @Bean
    public NewTopic topicApplicationDenied() {
        return TopicBuilder
                .name("application-denied")
                .partitions(2)
                .replicas(1)
                .build();
    }

}
