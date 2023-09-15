package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InwardIssue {
    public String id;
    public String key;
    public String self;
    public Fields fields;
}
