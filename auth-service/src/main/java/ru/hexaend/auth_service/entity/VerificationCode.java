package ru.hexaend.auth_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "verification_codes")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "verification_code_seq", sequenceName = "verification_code_seq", allocationSize = 1)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne()
    private User user;
}
