package com.bbva.dto.job.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class JobBasicInfoSelectDtoResponse {
    public int jobId;
    public String jobName;
    public String folderName;
    public String applicationName;
    public String dataprocJobName;
    public String runAsName;
    public int classificationType;
    public String classificationName; //element_name
    public String invetoriedType;
    public int monitoringDomainId;
    public String monitoringDomainName;
    public int monitoringProjectId;
    public String monitoringProjectName;

/*
    public int maxWaitNumber;
    public String cmdLine;
    public Date createAuditDate;
    public String createAuditDate_S;
    public String createAuditUser;
    public Date updateAuditDate;
    public String updateAuditDate_S;
    public String updateAuditUser;*/
}
