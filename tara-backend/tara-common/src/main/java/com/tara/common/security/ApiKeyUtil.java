package com.tara.common.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

public final class ApiKeyUtil {

    private static final String PREFIX = "tara_live_";
    private static final SecureRandom RANDOM = new SecureRandom();

    private ApiKeyUtil() {}

    public static GeneratedKey generateKey() {
        byte[] randomBytes = new byte[24];
        RANDOM.nextBytes(randomBytes);
        String hex = HexFormat.of().formatHex(randomBytes);
        String rawKey = PREFIX + hex;
        String keyHash = hashKey(rawKey);
        return new GeneratedKey(rawKey, PREFIX, keyHash);
    }

    public static String hashKey(String rawKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(rawKey.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    public static boolean isValidFormat(String rawKey) {
        return rawKey != null && rawKey.startsWith(PREFIX) && rawKey.length() == PREFIX.length() + 48;
    }

    public record GeneratedKey(String rawKey, String prefix, String keyHash) {}
}
