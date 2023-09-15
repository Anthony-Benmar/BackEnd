package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class IssueBulkDto {
    public List<IssueUpdate> issueUpdates;
}
