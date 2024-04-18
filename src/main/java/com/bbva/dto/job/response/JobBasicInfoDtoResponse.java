package com.bbva.dto.job.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class JobBasicInfoDtoResponse {
    private int jobId;
    private String jobName;
    private String folderName;
    private String applicationName;
    private String subApplicationName;
    private String runAsName;
    private String nodeId;
    private int maxWaitNumber;
    private String cmdLine;
    private Date createAuditDate;
    private String createAuditUser;
    private Date updateAuditDate;
    private String updateAuditUser;
}
