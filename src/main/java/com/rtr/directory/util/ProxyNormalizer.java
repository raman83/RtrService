package com.rtr.directory.util;

public final class ProxyNormalizer {
    private ProxyNormalizer() {}

    public static String normalize(String proxyType, String proxyValue) {
        if (proxyType == null || proxyValue == null) return proxyValue;
        if ("PHONE".equalsIgnoreCase(proxyType)) {
            String digits = proxyValue.replaceAll("[^0-9+]", "");
            if (!digits.startsWith("+")) {
                digits = "+1" + digits;
            }
            return digits;
        }
        if ("EMAIL".equalsIgnoreCase(proxyType)) {
            return proxyValue.trim().toLowerCase();
        }
        return proxyValue.trim();
    }
}
