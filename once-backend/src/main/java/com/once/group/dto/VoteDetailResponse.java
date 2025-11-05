package com.once.group.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class VoteDetailResponse {
    private Long voteId;
    private String title;
    private LocalDateTime createdAt;
    private List<OptionResponse> options;

    @Getter
    @Setter
    public static class OptionResponse {
        private String optionDate;
        private int voteCount;
    }
}
