package com.bbva.dto.jira.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JiraResDTO {
    private String isValid;
    private String isWarning;
    private String helpMessage;
    private String group;
}
