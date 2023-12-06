package com.bbva.dto.batch.request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertAJIFJobExecutionRequest {
    private Long jobId;
    private String jobName;
    private String orderId;
    private String folder;
    private String application;
    private String subApplication;
    private String orderDate;
    private String startTime;
    private String endTime;
    private String host;
    private String runAs;
    private String executionStatus;
    private String sourceOrigin;
    private String createDate;
    private String updateDate;
}
