package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IssueDto {
    public String expand;
    public String id;
    public String self;
    public String key;
    public Fields fields;
}

