package com.bbva.dto.batch.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertJobExecutionStatusRequest {
    private String jobName;
    private String schedtable;
    private String application;
    private String subApplication;
    private String runAs;
    private String orderId;
    private String odate;
    private String startTime;
    private String endTime;
    private String runTime;
    private String runCounter;
    private String endedStatus;
    private String host;
    private String cputime;
}
