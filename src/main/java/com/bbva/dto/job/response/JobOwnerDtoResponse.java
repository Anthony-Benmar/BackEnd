package com.bbva.dto.job.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JobOwnerDtoResponse {
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
