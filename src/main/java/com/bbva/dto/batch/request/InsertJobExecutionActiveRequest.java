package com.bbva.dto.batch.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertJobExecutionActiveRequest {
    private String orderId;
    private String jobName;
    private String schedtable;
    private String application;
    private String subApplication;
    private String odate;
    private String startTime;
    private String endTime;
    private String host;
    private String runAs;
    private String status;
}
