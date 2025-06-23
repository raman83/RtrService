package com.rtr.service;

import com.rtr.dto.Pacs008Message;
import com.common.iso.CanonicalPayment;
import com.rtr.dto.Pacs002Response;
import com.rtr.producer.Pacs002Producer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RtrProcessor {

    private final Pacs002Producer pacs002Producer;

    public void process(CanonicalPayment payment) {
        Pacs008Message pacs008 = Pacs008Message.builder()
                .messageId(UUID.randomUUID().toString())
                .endToEndId(payment.getPaymentId())
                .debtorAccount(payment.getDebtorAccount())
                .creditorAccount(payment.getCreditorAccount())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .proxyType(payment.getProxyType())
                .proxyValue(payment.getProxyValue())
                .sentAt(Instant.now())
                .build();

        log.info("Simulated pacs.008: {}", pacs008);

        boolean approved = payment.getAmount().compareTo(BigDecimal.valueOf(500)) <= 0;
        String status = approved ? "ACCEPTED" : "REJECTED";
        String reason = approved ? null : "LIMIT_EXCEEDED";

        Pacs002Response response = Pacs002Response.builder()
        		  .originalPaymentId(pacs008.getMessageId())
                  .transactionStatus(status)
                  .reasonCode(reason)
                  .fromAccount(pacs008.getDebtorAccount())
                  .toAccount(pacs008.getCreditorAccount())
                  .amount(pacs008.getAmount())
                  .currency(pacs008.getCurrency())
                  .settlementTimestamp(Instant.now())
                  .build();

        log.info("Mock pacs.002 (Payments Canada): {}", response);

        pacs002Producer.send(response);
    }
}
