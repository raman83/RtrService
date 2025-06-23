package com.common.iso;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CanonicalPayment {
    private String paymentId;
    private String debtorName;
    private String debtorAccount;
    private String creditorName;
    private String creditorAccount;
    private String creditorBank;
    private BigDecimal amount;
    private String currency;
    private String purpose;
    private LocalDate requestedExecutionDate;
    private String channel;

    // RTR-specific
    private String proxyType;
    private String proxyValue;
    private String rtrStatus;
    private String rtrReasonCode;
}
