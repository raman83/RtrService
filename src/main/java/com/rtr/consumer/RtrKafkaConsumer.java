package com.rtr.consumer;

import com.common.iso.CanonicalPayment;
import com.rtr.service.RtrProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RtrKafkaConsumer {

    private final RtrProcessor rtrProcessor;

    @KafkaListener(topics = "rtr.payment.requested", groupId = "rtr-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, CanonicalPayment> record) {
        CanonicalPayment payment = record.value();
        log.info("Received RTR payment: {}", payment);
        rtrProcessor.process(payment);
    }
}
