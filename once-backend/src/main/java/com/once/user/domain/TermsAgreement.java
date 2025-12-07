/**
 * File: TermsAgreement.java
 * Description:
 *  - 사용자의 약관 동의 정보를 저장하는 도메인 모델
 *  - 동의 종류, 동의 여부, 동의 버전, 동의 시각을 기록함
 */

package com.once.user.domain;

import java.time.LocalDateTime;

public class TermsAgreement {

    private Long id;
    private Long userId;
    private String termType;        // 약관 종류 (예: SERVICE, PRIVACY)
    private Boolean agreed;         // 동의 여부
    private String agreedVersion;   // 약관 버전
    private LocalDateTime agreedAt; // 동의 일시

    // getter/setter
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