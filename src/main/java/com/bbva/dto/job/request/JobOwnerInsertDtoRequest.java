package com.bbva.dto.job.request;

import lombok.*;

@Getter
@Setter
@Data
public class JobOwnerInsertDtoRequest {
    private Integer jobId;
    private Integer createdDomainId;
    private String createdSdatoolId;
    private String createdDevUser;
    private Integer monitoringDomainId;
    private String monitoringSdatoolId;
    private String monitoringDevUser;
    private Integer jobStatusType;
    private String createAuditDate;
    private String createAuditUser;
    private String updateAuditDate;
    private String updateAuditUser;
}
