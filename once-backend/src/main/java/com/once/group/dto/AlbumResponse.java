package com.once.group.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class AlbumResponse {
    private Long albumId;
    private Long groupId;
    private String title;
    private String description;
    private String imageUrl;
    private LocalDateTime createdAt;

}