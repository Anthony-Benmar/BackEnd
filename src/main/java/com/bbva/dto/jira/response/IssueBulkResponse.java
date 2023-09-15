package com.bbva.dto.jira.response;

import com.bbva.dto.jira.request.IssueDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class IssueBulkResponse {
    public List<IssueDto> issues;
}
