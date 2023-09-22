package com.bbva.dto.usecasetracking;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class GetUseCaseTrackingJobsResponseDto {
    private int count;
    private int pages_amount;
    private List<UseCaseTrackingData> useCaseTracking;
    private StatisticsData globalStatistics;
}
