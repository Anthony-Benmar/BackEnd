package com.bbva.dto.job.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JobMonitoringRequestFilterDtoRequest {
    private Integer page;
    private Integer records_amount;

    private Integer domainId;
    private String toSdatoolId;//projectId
    private Integer jobId; //Join con job_basic_info
    private Integer statusType;
}
