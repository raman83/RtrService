package com.rtr.directory.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "proxy_aliases",
       uniqueConstraints = @UniqueConstraint(name = "uq_proxy_type_value", columnNames = {"proxyType","proxyValue"}))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProxyAlias {

    @Id @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 16)
    private String proxyType;             // PHONE, EMAIL, BUSINESS_ID

    @Column(nullable = false, length = 64)
    private String proxyValue;            // normalized (E.164 for PHONE, lowercase for EMAIL)

    @Column(nullable = false, length = 16)
    private String linkType;              // INTERNAL or EXTERNAL

    // INTERNAL
    private UUID internalAccountId;       // if linkType == INTERNAL

    // EXTERNAL
    @Column(length = 3)
    private String institutionNumber;     // if linkType == EXTERNAL
    @Column(length = 5)
    private String transitNumber;
    private String accountNumber;

    // owner info (optional but useful)
    private String customerId;

    @Column(nullable = false, length = 16)
    private String status;                // REGISTERED, VERIFIED, DISABLED

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
