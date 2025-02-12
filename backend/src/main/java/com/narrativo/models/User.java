package com.narrativo.models;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(nullable = false, unique = true) // Ensure email is unique
    private String email;

    private String password;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    public enum Role {
        USER,
        ADMIN
    }

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER; // Default role is USER
}