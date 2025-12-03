package com.once.ai.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AiCategoryRequest {
    private List<String> user_categories;
    private List<String> group_categories;
}