package ru.hexaend.auth_service.security;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@ConfigurationProperties(prefix = "rsa")
@Data
public class RsaPropertiesConfig {
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
}
