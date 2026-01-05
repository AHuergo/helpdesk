package com.anahuergo.helpdesk.dto;

import com.anahuergo.helpdesk.domain.User;
import com.anahuergo.helpdesk.domain.UserRole;
import java.time.LocalDateTime;

public class UserResponse {

    private Long id;
    private String email;
    private String name;
    private String surname;
    private UserRole role;
    private LocalDateTime createdAt;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
    }

    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public UserRole getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }

}