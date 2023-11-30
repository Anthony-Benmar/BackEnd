package com.bbva.dto.batch.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
public class JobExecutionFilterData {
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
    private Integer errorType;
    private String errorDesc;
    private Integer errorReason;
    private String errorReasonDesc;
    private Integer recordsCount;
    private String updatedBy;
    private String updatedAt;
    private String createdBy;
    private String createdAt;
    private String sdatoolId;
    private String domain;
    private Boolean isTypified;
    private Integer typified;
    private Integer withoutTypified;
    private String logArgos;
    private Integer runCounter;
}
