package com.bbva.dto.job.request;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

@Setter
@Getter
public class JobAdditionalDtoRequest {
    private Integer jobId;
    private Integer createdProjectId;
    private String createdDevEmail;
    private Integer monitoringProjectId;
    private String monitoringDevEmail;
    private Integer classificationType;//reclassificationType;
    private Integer criticalRouteType;
    private String jobFunctionalDesc;
    private String createAuditUser;
}
