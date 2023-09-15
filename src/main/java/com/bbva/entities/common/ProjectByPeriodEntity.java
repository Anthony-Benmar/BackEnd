package com.bbva.entities.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectByPeriodEntity {
    private String project_id;
    private String sdatool_id;
    private String project_name;
    private String status_type;
    private String period_id;
}
