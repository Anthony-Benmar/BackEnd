package com.bbva.dto.batch.request;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.sql.Date;

@Getter
@Setter
public class InsertBatchIssueActionsDtoRequest {
    @Nullable
    private Integer issueActionsId;
    private Integer jobId;
    //private String jobName;
    private String folderName;
    private String devEmail;
    private String startDate;
    private String endDate;
    @Nullable
    private Integer statusType;
    private String commentActionsDesc;
    private String createAuditUser;
    @Nullable
    private String updateAuditUser;
}