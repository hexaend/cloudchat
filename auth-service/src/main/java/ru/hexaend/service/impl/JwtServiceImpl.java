package ru.hexaend.service.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.hexaend.service.JwtService;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final PasswordEncoder passwordEncoder;

    SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode("replacethiswithyourownbase64encodedsecretkey"));

    @Override
    public String generateAccessToken(String username) {
        final Date accessTokenExpiration = new Date(System.currentTimeMillis() + 15 * 60 * 1000); // 15 minutes
        return Jwts.builder()
                .subject(username)
                .signWith(key)
                .expiration(accessTokenExpiration)
                .claim("role", "USER") // TODO: get user role from database
                .claim("firstName", "John") // TODO: get user first name from database
                .claim("lastName", "Doe") // TODO: get user last name from database
                .compact();
    }

    public Jwt<?, ?> decodeToken(String token) {
        try {
            log.info("Decoding token: {}", token);
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parse(token);
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
        } catch (Exception e) {
            log.error("invalid token", e);
        }

        return null;
    }

}