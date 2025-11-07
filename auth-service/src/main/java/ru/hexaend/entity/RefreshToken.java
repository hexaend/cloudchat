package ru.hexaend.entity;

import java.time.Instant;

public class RefreshToken {

    private Long id;
    private String token;
    private Long userId;
    private Instant expiryDate;

}
