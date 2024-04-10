package com.bbva.dto.project.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectInfoSelectResponse {
    public Integer projectId;
    public String sdatoolId;
    public String projectName;
    public String projectDesc;
    public String portafolioCode;
    public Integer regulatoryType;
    public Integer ttvType;
    public Integer domainId;
    public String domainName;
    public Integer projectType;
    public String projectTypeDesc;
    public Integer categoryType;
    public Integer classificationType;
    public String classificationTypeDesc;
    public Integer startPiId;
    public Integer endPiId;
    public Integer finalStartPiId;
    public Integer finalEndPiId;
    public Integer statusType;
    public String statusTypeDesc;
    private Integer wowType;
}
