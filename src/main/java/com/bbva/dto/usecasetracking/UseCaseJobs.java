package com.bbva.dto.usecasetracking;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UseCaseJobs {
    private String job_name;
    private String execution_status;
    private String functional_description;
    private String additional_functional_description;
    private String order_date;
    private String start_time;
    private String end_time;
}
