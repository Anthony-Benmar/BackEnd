package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Issuelink {
    public String id;
    public String self;
    public Type type;
    public InwardIssue inwardIssue;
}
