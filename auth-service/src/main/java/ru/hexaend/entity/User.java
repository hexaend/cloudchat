package ru.hexaend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name="first_name", nullable = false, unique = false)
    private String firstName;

    @Column(name="last_name", nullable = false, unique = false)
    private String lastName;

    // TODO: change to Enum / change to ManyToMany relationship
    @Column(name="role", nullable = false, unique = false)
    private String role;
}
