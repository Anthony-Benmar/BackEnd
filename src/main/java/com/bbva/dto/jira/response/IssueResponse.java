package com.bbva.dto.jira.response;

import com.bbva.dto.jira.request.Fields;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IssueResponse {
    private String expand;
    private String id;
    private String self;
    private String key;
    private Fields fields;
}