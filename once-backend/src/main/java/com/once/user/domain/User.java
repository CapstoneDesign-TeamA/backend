package com.once.user.domain;

import lombok.Setter;

import java.time.LocalDateTime;

@Setter
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String nickname;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() { return id; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public String getEmail() { return email; }

    public String getNickname() { return nickname; }

    public String getStatus() { return status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
}