package com.bbva.dto.project.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectCatalogDtoResponse {
    private String sdatoolId;
    private String projectName;
    private String sn1;
    private String sn1Desc;
    private String sn2;
    private String sn2ProjectId;
    private String codigo5Digitos; //sn2_project_code - sn2ProjectCode
}
