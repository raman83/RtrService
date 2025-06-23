package com.rtr.dto;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pacs008Message {
    private String messageId;
    private String endToEndId;
    private String debtorAccount;
    private String creditorAccount;
    private BigDecimal amount;
    private String currency;
    private String proxyType;
    private String proxyValue;
    private Instant sentAt;
}
