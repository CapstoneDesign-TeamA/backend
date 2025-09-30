package com.once.user.dto.domain;

public class UserInterest {
    private Long id;
    private Long userId;
    private String interest;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getInterest() { return interest; }
    public void setInterest(String interest) { this.interest = interest; }
}