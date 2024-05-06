package com.bbva.dto.project.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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
    public Integer projectType;
    public Integer categoryType;
    public Integer classificationType;
    public Integer startPiId;
    public Integer endPiId;
    public Integer finalStartPiId;
    public Integer finalEndPiId;
    public Integer statusType;
    private Integer wowType;
    private String wowName;
    private Integer countryPriorityType;
    private Date createAuditDate;
    private String createAuditDate_S;
    private String createAuditUser;
    private Date updateAuditDate;
    private String updateAuditDate_S;
    private String updateAuditUser;


    public String domainName;
    public String projectTypeDesc;
    public String classificationTypeDesc;


    public String statusTypeDesc;
}
