package com.bbva.dto.jira.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DmJiraValidatorMessageDTO {

    private int ruleId;
    private String rule;
    private String message;
    private String status;
    private boolean visible;
    private int order;
    private List<String> details;


}