package com.once.user.domain;

import java.time.LocalDateTime;

public class TermsAgreement {
    private Long id;
    private Long userId;
    private String termType;
    private Boolean agreed;
    private String agreedVersion;
    private LocalDateTime agreedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTermType() { return termType; }
    public void setTermType(String termType) { this.termType = termType; }

    public Boolean getAgreed() { return agreed; }
    public void setAgreed(Boolean agreed) { this.agreed = agreed; }

    public String getAgreedVersion() { return agreedVersion; }
    public void setAgreedVersion(String agreedVersion) { this.agreedVersion = agreedVersion; }

    public LocalDateTime getAgreedAt() { return agreedAt; }
    public void setAgreedAt(LocalDateTime agreedAt) { this.agreedAt = agreedAt; }
}