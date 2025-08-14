package com.rtr.directory.dto;

import java.util.UUID;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterAliasRequest {
    private String customerId;
    private String proxyType;           // PHONE/EMAIL/BUSINESS_ID
    private String proxyValue;          // raw; we will normalize

    private String linkType;            // INTERNAL/EXTERNAL

    // for INTERNAL
    private UUID internalAccountId;

    // for EXTERNAL
    private String institutionNumber;
    private String transitNumber;
    private String accountNumber;
}
