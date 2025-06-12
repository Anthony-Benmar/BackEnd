package com.bbva.dto.ada.request;

import com.bbva.dto.PaginationMasterDtoRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdaJobExecutionFilterRequestDTO extends PaginationMasterDtoRequest {
    private String jobName;
    private String startDate;
    private String endDate;
    private String frequency;
    private String isTransferred;
    private String jobType;
    private String serverExecution;
    private String domain;
}
