package com.bbva.dto.usecasetracking;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UseCaseTrackingData {
    private int use_case_id;
    private String name;
    private int domain_id;
    private int subgroup_use_case;
    private List<UseCaseJobs> useCaseJobs;
    private StatisticsData statistics;
}
