/**
 * File: AlbumResponse.java
 * Description:
 *  - 앨범 조회/응답 DTO
 *  - 앨범 기본 정보 + 생성 시각 전달
 */

package com.once.group.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class AlbumResponse {

    private Long albumId;          // 앨범 ID
    private Long groupId;          // 그룹 ID
    private String title;          // 앨범 제목
    private String description;    // 앨범 설명
    private String imageUrl;       // 대표 이미지 URL
    private LocalDateTime createdAt; // 생성 시각
}