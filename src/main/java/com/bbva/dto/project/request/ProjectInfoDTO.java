package com.bbva.dto.project.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectInfoDTO {
    private Integer projectId;
    private String sdatoolId;
    private String projectName;
    private String projectDesc;
    private String portafolioCode;
    private Integer regulatoryType;
    private Integer ttvType;
    private String domainId;
    private Integer domainType;
    private Integer projectType;
    private Integer categoryType;
    private Integer classificationType;
    private Integer startPiId;
    private Integer endPiId;
    private Integer finalStartPiId;
    private Integer finalEndPiId;
    private Integer wowType;
    private Integer countryPriorityType;
    private String createAuditUser;
    private Integer statusType;
}
