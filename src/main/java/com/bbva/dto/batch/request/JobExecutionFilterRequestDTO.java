package com.bbva.dto.batch.request;

import com.bbva.dto.PaginationMasterDtoRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobExecutionFilterRequestDTO extends PaginationMasterDtoRequest {
    private String jobName;
    private String startDate;
    private String endDate;
    private String folder;
    private String dataproc;
    private String orderId;
    private String projectName;
    private String sdatoolId;
    private String domain;
    private Boolean isTypified;
}
