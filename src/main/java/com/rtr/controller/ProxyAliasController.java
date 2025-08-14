package com.rtr.controller;

import com.rtr.directory.dto.RegisterAliasRequest;
import com.rtr.directory.dto.RegisterAliasResponse;
import com.rtr.directory.model.ProxyAlias;
import com.rtr.directory.service.ProxyDirectoryService;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/proxy-aliases")
@RequiredArgsConstructor
public class ProxyAliasController {

    private final ProxyDirectoryService service;

    @PostMapping
    public ResponseEntity<RegisterAliasResponse> register(@RequestBody RegisterAliasRequest req) {
        RegisterAliasResponse resp = service.register(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<Void> verify(@PathVariable("id") UUID id) {
        service.verify(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/resolve")
    public ResponseEntity<?> resolve(@RequestParam String proxyType, @RequestParam String proxyValue) {
        Optional<ProxyAlias> opt = service.resolve(proxyType, proxyValue);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        ProxyAlias alias = opt.get();
        return ResponseEntity.ok(Map.of(
            "proxyType", alias.getProxyType(),
            "proxyValue", alias.getProxyValue(),
            "linkType", alias.getLinkType(),
            "status", alias.getStatus(),
            "internalAccountId", alias.getInternalAccountId(),
            "institutionNumber", alias.getInstitutionNumber(),
            "transitNumber", alias.getTransitNumber(),
            "accountNumber", alias.getAccountNumber()
        ));
    }
}
