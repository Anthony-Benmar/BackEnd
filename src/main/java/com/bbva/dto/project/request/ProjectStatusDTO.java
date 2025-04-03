package com.bbva.dto.project.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class ProjectStatusDTO {
    private Integer projectId;
    private String statusName;
    private Integer statusId;
    private Date startDate;
}