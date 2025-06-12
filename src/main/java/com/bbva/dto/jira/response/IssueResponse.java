package com.bbva.dto.jira.response;

import com.bbva.dto.jira.request.Fields;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IssueResponse {
    public String expand;
    public String id;
    public String self;
    public String key;
    public Fields fields;
}