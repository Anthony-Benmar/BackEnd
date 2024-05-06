package com.bbva.dto.job.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class JobMonitoringRequestInsertDtoRequest {
    private Integer jobId;
    private String fromSdatoolId;
    private String fromDevEmail;
    private String toSdatoolId;
    private String toDevEmail;
    private Date startDate;
    private Date endDate;
    private Integer statusType;
    private String commentRequestDesc;
    private String createAuditUser;
}
