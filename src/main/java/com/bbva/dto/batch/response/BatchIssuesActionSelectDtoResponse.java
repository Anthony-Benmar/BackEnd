package com.bbva.dto.batch.response;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.Date;

@Setter
@Getter
public class BatchIssuesActionSelectDtoResponse {
    private Integer issueActionsId;
    private Integer jobId;
    private String jobName;
    private String folderName;
    private String devEmail;
    private String startDate;
    private String endDate;
    private Integer statusType;
    private String commentActionsDesc;
    private String createAuditUser;
    private String createAuditDate;
    private String updateAuditUser;
    private String updateAuditDate;
}
