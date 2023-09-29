package com.bbva.dto.usecasetracking;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GetUseTrackingJobsSPResponse {
    private int use_case_id;
    private String name;
    private int domain_id;
    private int subgroup_use_case;
    private String job_name;
    private String execution_status;
    private String functional_description;
    private String additional_functional_description;
    private String order_date;
    private String start_time;
    private String end_time;
    private String view_type;
    private Integer records_count;
}
