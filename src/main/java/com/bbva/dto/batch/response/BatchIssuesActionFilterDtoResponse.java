package com.bbva.dto.batch.response;

import com.bbva.dto.job.response.JobMonitoringRequestSelectDtoResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BatchIssuesActionFilterDtoResponse {
    public Integer count;
    public Integer pages_amount;
    public List<BatchIssuesActionSelectDtoResponse> data;
}
