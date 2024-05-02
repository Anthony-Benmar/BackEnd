package com.bbva.dto.job.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class JobMonitoringDtoResponse {
    private Integer monitoringRequestId;
    private Integer jobId;
    private String fromSdatoolId;
    private String fromDevEmail;
    private String toSdatoolId;
    private String toDevEmail;
    private Date startDate;
    private Date endDate;
    private Integer statusType;
    private String commentRequestDesc;
    private Date createAuditDate;
    private String createAuditUser;
    private Date updateAuditDate;
    private String updateAuditUser;
}

