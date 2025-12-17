package com.anahuergo.helpdesk.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column (nullable = false)
    private String password;

    @Column (nullable = false)
    private String name;

    @Column (nullable = false)
    private String surname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.REQUESTER;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
