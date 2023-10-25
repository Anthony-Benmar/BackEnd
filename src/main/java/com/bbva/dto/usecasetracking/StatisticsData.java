package com.bbva.dto.usecasetracking;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StatisticsData {
    private Integer total;
    private Integer successful;
    private Integer inProgress;
    private Integer canceled;
}
