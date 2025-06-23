package com.rtr.producer;

import com.rtr.dto.Pacs002Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Pacs002Producer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String STATUS_TOPIC = "rtr.payment.status";

    public void send(Pacs002Response response) {
        kafkaTemplate.send(STATUS_TOPIC, response);
        log.info("Published pacs.002 to {}: {}", STATUS_TOPIC, response);
    }
}
