package com.once.group.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class VoteResultResponse {

    private Long voteId;
    private String title;
    private List<ResultItem> results;

    @Getter
    @Setter
    public static class ResultItem {
        private String optionDate;
        private int voteCount;
    }
}
