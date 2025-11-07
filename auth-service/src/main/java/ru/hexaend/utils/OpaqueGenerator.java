package ru.hexaend.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

// TODO: change JWT reload token mechanism to opaque tokens
@Component
public class OpaqueGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public String generateOpaqueToken() {
        return generateOpaqueToken(32);
    }

    public String generateOpaqueToken(int byteLength) {
        byte[] randomBytes = new byte[byteLength];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

}
