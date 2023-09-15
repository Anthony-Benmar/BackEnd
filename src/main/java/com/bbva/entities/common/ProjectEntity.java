package com.bbva.entities.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ProjectEntity {
    private String project_id;
    private String sdatool_id;
    private String project_name;
    private String status_type;
}
