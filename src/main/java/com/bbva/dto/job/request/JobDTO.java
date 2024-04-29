package com.bbva.dto.job.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class JobDTO {
    private int jobId;
    private String jobName;
    private String folderName;
    private String applicationName;
    private String subApplicationName;
    private String dataprocJobName;
    private String runAsName;
    private String nodeId;
    private int maxWaitNumber;
    private String cmdLineDesc;
    private String classificationType;
    private String classificationName;
    private int monitoringDomainId;
    private String monitoringDomainName;
    private int monitoringProjectId;
    private String monitoringProjectName;
    private String monitoringDevEmail;
    private int createdDomainId;
    private String createdDomainName;
    private int createdProjectId;
    private String createdProjectName;
    private String createdDevEmail;
    private Date createAuditDate;
    private String createAuditUser;
    private Date updateAuditDate;
    private String updateAuditUser;
}
