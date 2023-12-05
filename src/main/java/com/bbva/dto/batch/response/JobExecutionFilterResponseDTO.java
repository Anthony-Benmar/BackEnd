package com.bbva.dto.batch.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JobExecutionFilterResponseDTO {
    private int count;
    private int pages_amount;
    private List<JobExecutionFilterData> data;
    private StatisticsData statistics;
}
