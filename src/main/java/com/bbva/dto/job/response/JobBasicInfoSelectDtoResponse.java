package com.bbva.dto.job.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobBasicInfoSelectDtoResponse {
    public Integer jobId;
    public String jobName;
    public String folderName;
    public String applicationName;
    public String dataprocJobName;
    public String runAsName;
    public Integer classificationType;
    public String classificationName;
    public String invetoriedType;
    public Integer monitoringDomainId;
    public String monitoringDomainName;
    public Integer monitoringProjectId;
    public String monitoringProjectName;
    private String monitoringDevEmail;
    private String subApplicationName;
    private Integer flagCriticalRoute;
}
