package com.bbva.dto.jira.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JiraMessageResponseDTO {
    private int ruleId;
    private String rule;
    private String message;
    private String status;
    private Boolean visible = true;
}
