package com.bbva.dto.batch.request;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

@Getter
@Setter
public class InsertReliabilityIncidenceDTO {
    private String jobName;
    private String orderDate;
    private String orderId;
    private Integer errorType;
    private Integer errorReason;
    private String solutionDetail;
    private String employeeId;
    private String logArgos;
    private Integer runCounter;
    private String ticketJira;
    private InsertBatchIssueActionsDtoRequest dataIssueActions;
}
