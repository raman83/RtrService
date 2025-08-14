package com.rtr.directory.dto;

import java.util.UUID;
import lombok.*;


@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterAliasResponse {
    private UUID id;
    private String proxyType;
    private String proxyValue; // normalized
    private String linkType;
    private String status;
}