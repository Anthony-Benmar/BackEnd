package com.bbva.dto.batch.response;

import lombok.*;

import java.util.Date;

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
    private Integer solutionType;
    private String solutionDesc;
    private String solutionDetail;
    private Integer jobType;
    private String jobDesc;
    private Integer errorType;
    private String errorDesc;
    private String errorDetails;
    private Integer recordsCount;
    private String updatedBy;
    private String updatedAt;
    private String createdBy;
    private String createdAt;
    private String sdatoolId;
}
