package com.bbva.dto.job.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JobAdditionalDtoResponse {
    private Integer jobId;
    private Integer reclassificationType;
    private Integer criticalRouteType;
    private String jobFunctionalDesc;
}
