package com.rtr.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.common.iso.CanonicalPayment;
import com.rtr.service.RtrProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rtr")
@Slf4j
public class RtrPaymentController {

    private final RtrProcessor rtrProcessor;

    @PostMapping("/process")
    public String processPayment(@RequestBody CanonicalPayment payment) {
        log.info("Received CanonicalPayment via REST: {}", payment);
        rtrProcessor.process(payment);
        return "RTR Payment processed via service (mock pacs.008/002 sent)";
    }
}
