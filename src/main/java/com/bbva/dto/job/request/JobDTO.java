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
    private Integer maxWaitNumber;
    private String cmdLineDesc;
    private String classificationType;
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
    private Integer criticalRouteType;
    private String createdDevEmail;
    private Date createAuditDate;
    private String createAuditDate_S;
    private String createAuditUser;
    private Date updateAuditDate;
    private String updateAuditDate_S;
    private String updateAuditUser;
}
