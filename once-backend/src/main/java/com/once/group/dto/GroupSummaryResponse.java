package com.once.group.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupSummaryResponse {
    private Long groupId;
    private String name;
    private int memberCount;
    private String lastActive;
}
