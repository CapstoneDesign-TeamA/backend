/**
 * File: UserInterest.java
 * Description:
 *  - 사용자별 관심사를 저장하는 도메인 클래스
 *  - 각 사용자가 선택한 관심사(카테고리)를 1:N 구조로 보관함
 */

package com.once.user.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInterest {

    private Long id;       // 기본 키
    private Long userId;   // 사용자 ID (FK)
    private String interest; // 관심사 이름
}