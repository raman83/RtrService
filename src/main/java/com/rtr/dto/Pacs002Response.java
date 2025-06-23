package com.rtr.dto;
import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pacs002Response {
    private String originalMessageId;
    private String status;         // e.g., ACCEPTED, REJECTED
    private String reasonCode;     // e.g., LIMIT_EXCEEDED, INVALID_PROXY
    private Instant responseTime;
}
