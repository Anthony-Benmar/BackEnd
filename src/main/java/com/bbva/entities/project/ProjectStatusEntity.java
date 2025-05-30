package com.bbva.entities.project;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ProjectStatusEntity {
    private Integer projectId;
    private Integer statusId;
    private String statusName;
    private Date startDate;
    private String startDateStr;
    private String piLargeName;
}
