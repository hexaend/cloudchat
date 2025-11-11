package ru.hexaend.auth_service.utils;

import lombok.experimental.UtilityClass;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@UtilityClass
public class StringUtils {

    private final SecureRandom secureRandom;

    // TODO: move to config
    private static final int DEFAULT_OPAQUE_BYTES = 64;
    private static final int DEFAULT_VERIFICATION_BYTES = 32;

    static {
        try {
            secureRandom = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    public String generateOpaqueToken() {
        return generateOpaqueToken(DEFAULT_OPAQUE_BYTES);
    }

    public String generateVerificationCode() {
        return generateOpaqueToken(DEFAULT_VERIFICATION_BYTES);
    }

    private String generateOpaqueToken(int byteLength) {
        byte[] randomBytes = new byte[byteLength];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

}
