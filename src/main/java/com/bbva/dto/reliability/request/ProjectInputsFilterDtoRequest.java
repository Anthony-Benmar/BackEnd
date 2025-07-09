package com.bbva.dto.reliability.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProjectInputsFilterDtoRequest {
    private String projectId;
    private String domainId;
    private String projectName;
    private String projectType;
    private String status;
    private String wow;
    private String startQ;
    private String endQ;
    private Integer page;           // seguirá allí, pero no se usa en el SP
    private Integer recordsAmount;
}
