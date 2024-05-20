package com.bbva.dto.batch.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobExecutionByIdDTO {
    private String jobName;
    private String folder;
    private String application;
    private String subApplication;
    private String jsonName;
    private String orderId;
    private String orderDate;
    private String startTime;
    private String endTime;
    private String executionStatus;
    private String projectName;
    private String scrumTeam;
    private String scrumMaster;
    private String solutionDetail;
    private String errorType;
    private String errorDesc;
    private String errorReason;
    private String errorReasonDesc;
    private String updatedBy;
    private String updatedAt;
    private String createdBy;
    private String createdAt;
    private String sdatoolId;
    private String domain;
    private Boolean isTypified;
    private String logArgos;
    private Integer runCounter;
    private String executionType;
    private String ticketJira;
    private Integer domainId;
    private Integer issueActionsId;
    private Integer jobId;
    private String folderName;
    private String devEmail;
    private String startDate;
    private String endDate;
    private String statusType;
    private String commentActionsDesc;
    private String createAuditDate;
    private String createAuditUser;
    private String updateAuditDate;
    private String updateAuditUser;
}
