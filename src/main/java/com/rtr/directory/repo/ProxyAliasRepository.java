package com.rtr.directory.repo;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.rtr.directory.model.ProxyAlias;

public interface ProxyAliasRepository extends JpaRepository<ProxyAlias, UUID> {
    Optional<ProxyAlias> findByProxyTypeAndProxyValueIgnoreCase(String proxyType, String proxyValue);
}
