package com.once.group.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupInviteInfo {
    private Long groupId;
    private String name;
    private String description;
    private String imageUrl;
    private Integer membersCount;
}

