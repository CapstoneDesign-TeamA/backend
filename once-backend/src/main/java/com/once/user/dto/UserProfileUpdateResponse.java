package com.once.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileUpdateResponse {
    private Long userId;
    private String name;
    private String email;
    private String profileImage;
    private String message;
}

