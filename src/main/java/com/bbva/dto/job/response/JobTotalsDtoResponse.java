package com.bbva.dto.job.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobTotalsDtoResponse {
    private int totalJobs;
    private int inventoriedJobs;
    private int criticalRouteJobs;
}
