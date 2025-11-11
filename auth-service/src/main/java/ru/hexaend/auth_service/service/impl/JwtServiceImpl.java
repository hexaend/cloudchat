package ru.hexaend.auth_service.service.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hexaend.auth_service.config.RsaPropertiesConfig;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.exception.JwtException;
import ru.hexaend.auth_service.service.interfaces.JwtService;

import java.text.ParseException;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final int accessTokenExpirationMinutes = 15;
    private final RsaPropertiesConfig rsaPropertiesConfig;


    @Override
    public String generateAccessToken(User user) {
        return generateToken(user, accessTokenExpirationMinutes * 60 * 1000);
    }


    @Override
    public String getUsernameFromToken(String jwtToken)  {
        SignedJWT signedJWT = null;
        try {
            signedJWT = SignedJWT.parse(jwtToken);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new JwtException("Failed to parse JWT token", e);
        }
    }

    private String generateToken(User user, long expirationMillis) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiration = new Date(nowMillis + expirationMillis);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                // TODO: move issuer to application properties
                .issuer("http://localhost:8081")
                .jwtID(String.valueOf(user.getId()))
                .issueTime(now)
                .expirationTime(expiration)
                .claim("authorities", user.getAuthorities())
                .claim("email", user.getEmail())
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims);
        RSASSASigner signer = new RSASSASigner(rsaPropertiesConfig.getPrivateKey());
        try {
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            throw new JwtException("Failed to sign JWT token", e);
        }

        return signedJWT.serialize();
    }
}