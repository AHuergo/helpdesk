package com.anahuergo.helpdesk.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "El Email debe ser válido")
    @NotBlank(message = "El Email es obligatorio")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password es obligatorio")
    @Size(min = 8, message = "La Password debe tener al menos 8 caracteres")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    @Column(nullable = false)
    private String surname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.REQUESTER;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

}
