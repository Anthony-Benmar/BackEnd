package com.bbva.dto.batch.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertReliabilityIncidenceDTO {
    private String jobName;
    private String orderDate;
    private String orderId;
    private Integer errorType;
    private String errorDetails;
    private String solutionDetail;
    private String employeeId;
}
