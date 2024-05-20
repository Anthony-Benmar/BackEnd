package com.bbva.dto.batch.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobExecutionByIdRequest {
    private String folder;
    private String orderId;
    private String jobName;
    private Integer runCounter;
}
