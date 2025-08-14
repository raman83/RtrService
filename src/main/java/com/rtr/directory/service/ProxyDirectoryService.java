package com.rtr.directory.service;

import com.rtr.directory.dto.RegisterAliasRequest;
import com.rtr.directory.dto.RegisterAliasResponse;
import com.rtr.directory.model.ProxyAlias;
import com.rtr.directory.repo.ProxyAliasRepository;
import com.rtr.directory.util.ProxyNormalizer;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProxyDirectoryService {

    private final ProxyAliasRepository repo;

    public RegisterAliasResponse register(RegisterAliasRequest req) {
        if (req.getProxyType() == null || req.getProxyValue() == null) {
            throw new IllegalArgumentException("proxyType and proxyValue are required");
        }
        if (req.getLinkType() == null) {
            throw new IllegalArgumentException("linkType is required");
        }
        String normalized = ProxyNormalizer.normalize(req.getProxyType(), req.getProxyValue());

        ProxyAlias alias = ProxyAlias.builder()
                .customerId(req.getCustomerId())
                .proxyType(req.getProxyType().toUpperCase())
                .proxyValue(normalized)
                .linkType(req.getLinkType().toUpperCase())
                .internalAccountId(req.getInternalAccountId())
                .institutionNumber(req.getInstitutionNumber())
                .transitNumber(req.getTransitNumber())
                .accountNumber(req.getAccountNumber())
                .status("REGISTERED") // set VERIFIED after OTP etc.
                .build();

        ProxyAlias saved = repo.save(alias);

        return RegisterAliasResponse.builder()
                .id(saved.getId())
                .proxyType(saved.getProxyType())
                .proxyValue(saved.getProxyValue())
                .linkType(saved.getLinkType())
                .status(saved.getStatus())
                .build();
    }

    public void verify(UUID id) {
        ProxyAlias alias = repo.findById(id).orElseThrow();
        alias.setStatus("VERIFIED");
        repo.save(alias);
    }

    public Optional<ProxyAlias> resolve(String proxyType, String proxyValue) {
        String normalized = ProxyNormalizer.normalize(proxyType, proxyValue);
        return repo.findByProxyTypeAndProxyValueIgnoreCase(proxyType, normalized);
    }
}
