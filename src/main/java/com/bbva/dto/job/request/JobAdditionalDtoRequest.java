package com.bbva.dto.job.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JobAdditionalDtoRequest {
    private Integer jobId;
    private Integer reclassificationType;
    private Integer criticalRouteType;
    private String jobFunctionalDesc;
    private String createAuditUser;
}
