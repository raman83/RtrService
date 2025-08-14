package com.rtr.service;

import com.common.iso.CanonicalPayment;
import com.rtr.dto.Pacs002Response;
import com.rtr.dto.Pacs008Message;
import com.rtr.producer.Pacs002Producer;
import com.rtr.directory.model.ProxyAlias;
import com.rtr.directory.service.ProxyDirectoryService;
import com.payment.client.AccountClient;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RtrProcessor {

    private final ProxyDirectoryService directory;
    private final Pacs002Producer pacs002Producer;
    private final AccountClient accountClient; // Feign to Account service

    // demo rule: approve <= 600 for external RTR
    private static final BigDecimal EXTERNAL_LIMIT = new BigDecimal("600");

    public void process(CanonicalPayment payment) {
        // --- Decide addressing mode ---
        boolean hasProxy = !isBlank(payment.getProxyType()) && !isBlank(payment.getProxyValue());
        boolean hasExternalRouting =
                !isBlank(payment.getCreditorInstitutionNumber())
             && !isBlank(payment.getCreditorTransitNumber())
             && !isBlank(payment.getCreditorAccount());

        if (hasProxy) {
            // MODE 1: proxy directory
            processViaProxy(payment);
            return;
        }

        if (hasExternalRouting) {
            // MODE 2: direct external (counterpartyId path)
            settleExternalDirect(payment);
            return;
        }

        // Optional: MODE 3: direct internal by UUID in creditorAccount (if you ever send it that way)
        if (!isBlank(payment.getCreditorAccount())) {
            try {
                UUID internalAccountId = UUID.fromString(payment.getCreditorAccount());
                settleInternalDirect(payment, internalAccountId);
                return;
            } catch (IllegalArgumentException ignore) {
                // not a UUID → fall through
            }
        }

        // If none matched, reject
        emitReject(payment, "ADDRESSING_MISSING");
    }

    // ---------- MODE 1: PROXY ----------

    private void processViaProxy(CanonicalPayment payment) {
        Optional<ProxyAlias> opt = directory.resolve(payment.getProxyType(), payment.getProxyValue());
        if (opt.isEmpty() || !"VERIFIED".equalsIgnoreCase(opt.get().getStatus())) {
            emitReject(payment, "PROXY_NOT_FOUND_OR_UNVERIFIED");
            return;
        }
        ProxyAlias alias = opt.get();

        if ("INTERNAL".equalsIgnoreCase(alias.getLinkType())) {
            settleInternalDirect(payment, alias.getInternalAccountId());
        } else {
            settleExternalDirect(payment,
                    alias.getInstitutionNumber(),
                    alias.getTransitNumber(),
                    alias.getAccountNumber(),
                    // for log/response “toAccount” string:
                    alias.getInstitutionNumber() + "-" + alias.getTransitNumber() + "-" + alias.getAccountNumber());
        }
    }

    // ---------- MODE 2: DIRECT EXTERNAL (counterpartyId) ----------

    private void settleExternalDirect(CanonicalPayment payment) {
        String inst = payment.getCreditorInstitutionNumber();
        String tran = payment.getCreditorTransitNumber();
        String acct = payment.getCreditorAccount();
        String toAcctDisplay = inst + "-" + tran + "-" + acct;

        settleExternalDirect(payment, inst, tran, acct, toAcctDisplay);
    }

    private void settleExternalDirect(CanonicalPayment payment,
                                      String inst, String tran, String acct, String toAcctDisplay) {
        Pacs008Message pacs008 = Pacs008Message.builder()
                .messageId(payment.getPaymentId().toString())
                .endToEndId(payment.getPaymentId().toString())
                .debtorAccount(payment.getDebtorAccount())
                .creditorAccount(toAcctDisplay)
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .proxyType(payment.getProxyType())   // may be null in direct mode
                .proxyValue(payment.getProxyValue()) // may be null in direct mode
                .sentAt(Instant.now())
                .build();
        log.info("RTR pacs.008 (direct external): {}", pacs008);

        boolean approved = payment.getAmount().compareTo(EXTERNAL_LIMIT) <= 0;
        String status = approved ? "SETTLED" : "FAILED";
        String reason = approved ? null : "LIMIT_EXCEEDED";

        Pacs002Response response = Pacs002Response.builder()
                .originalPaymentId(payment.getPaymentId().toString())
                .transactionStatus(status)
                .reasonCode(reason)
                .fromAccount(payment.getDebtorAccount())
                .toAccount(toAcctDisplay)
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .settlementTimestamp(Instant.now())
                .build();

        pacs002Producer.send(response);
        log.info("RTR pacs.002 (direct external): {}", response);
    }

    // ---------- MODE 3: DIRECT INTERNAL (by UUID) ----------

    private void settleInternalDirect(CanonicalPayment payment, UUID beneficiaryAccountId) {
        // Credit recipient internally
    	
    	
    	
     //   accountClient.creditAccount(beneficiaryAccountId, payment.getAmount(), null);

        Pacs002Response response = Pacs002Response.builder()
                .originalPaymentId(payment.getPaymentId().toString())
                .transactionStatus("SETTLED")
                .reasonCode(null)
                .fromAccount(payment.getDebtorAccount())
                .toAccount(beneficiaryAccountId.toString())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .settlementTimestamp(Instant.now())
                .build();

        pacs002Producer.send(response);
        log.info("RTR INTERNAL settled (direct/uuid): {}", response);
    }

    // ---------- Common helpers ----------

    private void emitReject(CanonicalPayment payment, String reason) {
        Pacs002Response response = Pacs002Response.builder()
                .originalPaymentId(payment.getPaymentId().toString())
                .transactionStatus("FAILED")
                .reasonCode(reason)
                .fromAccount(payment.getDebtorAccount())
                .toAccount(null)
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .settlementTimestamp(Instant.now())
                .build();
        pacs002Producer.send(response);
        log.warn("RTR reject: {}", response);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
