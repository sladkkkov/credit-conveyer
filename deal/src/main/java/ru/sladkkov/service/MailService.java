package ru.sladkkov.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final KafkaTemplate<String, String> kafkaTemplate;


    public void sendToTopic(String message, String topic) {
        kafkaTemplate.send(message, topic);

        log.info("Сообщение успешно отправлено в топик: " + topic);
    }
}
