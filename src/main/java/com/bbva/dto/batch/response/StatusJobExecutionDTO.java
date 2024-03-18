package com.bbva.dto.batch.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusJobExecutionDTO {
    private String jobName;
    private String orderId;
    private String orderDate;
    private Integer runCounter;
    private String executionStatus;
}
