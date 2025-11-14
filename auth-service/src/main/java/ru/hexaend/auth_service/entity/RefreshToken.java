package ru.hexaend.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id()
    @GeneratedValue(generator="refresh_token_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "refresh_token_seq", sequenceName = "refresh_token_sequence", allocationSize = 1)
    private Long id;

    // TODO: maybe hash the token for security
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt = Instant.now();

    // TODO: maybe soft delete with a boolean flag 'revoked', and notify user to email
}
