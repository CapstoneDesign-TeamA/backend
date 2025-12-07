/**
 * File: GroupDetailResponse.java
 * Description:
 *  - 그룹 상세 정보 응답 DTO
 *  - 그룹 기본 정보 + 멤버 목록 + 일정 목록 + 앨범 목록 포함
 */

package com.once.group.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class GroupDetailResponse {

    private Long groupId;                  // 그룹 ID
    private String name;                   // 그룹명
    private String description;            // 그룹 설명
    private String imageUrl;               // 대표 이미지 URL
    private List<String> members;          // 멤버 이름 목록
    private List<ScheduleResponse> schedules; // 그룹 일정 목록
    private List<String> albums;           // 앨범 제목 목록
}