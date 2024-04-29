package com.bbva.dto.job.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class JobBasicInfoByIdDtoResponse {
    private Integer jobId;
    private String jobName;
    private String folderName;
    private String applicationName;
    private String subApplicationName;
    private String dataprocJobName;
    private String runAsName;
    private String nodeId;
    private Integer maxWaitNumber;
    private String cmdLineDesc;
    private Integer classificationType;
    private String classificationName;
    private Integer monitoringDomainId;
    private String monitoringDomainName;
    private Integer monitoringProjectId;
    private String monitoringProjectName;
    private String monitoringDevEmail;
    private Integer createdDomainId;
    private String createdDomainName;
    private Integer createdProjectId;
    private String createdProjectName;
    private String createdDevEmail;
    private Date createAuditDate;
    private String createAuditUser;
    private Date updateAuditDate;
    private String updateAuditUser;
}






















