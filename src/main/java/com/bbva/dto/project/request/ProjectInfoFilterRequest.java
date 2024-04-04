package com.bbva.dto.project.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectInfoFilterRequest {
    public Integer records_amount;
    public Integer page;
    public Integer projectId;
    public String sdatoolId;
    public String projectName;
    public String portafolioCode;
    public Integer regulatoryType;
    public Integer ttvType;
    public String domainId;
    public Integer projectType;
    public Integer categoryType;
    public Integer classificationType;
    public Integer startPiId;
    public Integer endPiId;
    public Integer finalStartPiId;
    public Integer finalEndPiId;
}
