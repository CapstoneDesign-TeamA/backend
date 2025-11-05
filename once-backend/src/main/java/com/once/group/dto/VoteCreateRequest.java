package com.once.group.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class VoteCreateRequest {
    private String title;        // 투표 제목
    private List<String> dates;  // 날짜 등록 ex) ["2025-10-10", "2025-10-12"]
}
