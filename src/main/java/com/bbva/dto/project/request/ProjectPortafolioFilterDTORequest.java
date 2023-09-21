package com.bbva.dto.project.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectPortafolioFilterDTORequest {
    private String projectName;
    private String isRegulatory;
    private String domainType;
}
