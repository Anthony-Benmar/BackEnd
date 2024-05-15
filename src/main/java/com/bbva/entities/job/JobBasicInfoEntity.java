package com.bbva.entities.job;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Setter
@Getter
public class JobBasicInfoEntity {
    private Integer jobId;
    private String jobName;
    private String folderName;
    private String applicationName;
    private String subApplicationName;
    private Integer classificationType;
    private String dataprocJobName;
    private String runAsName;
    private String nodeId;
    private Integer maxWaitNumber;
    private String cmdLineDesc;
    private Integer jobStatusType;
    //Campos de auditoria
    private Date createAuditDate; //TIMESTAMP
    private String createAuditUser;
    private Date updateAuditDate;//TIMESTAMP
    private String updateAuditUser;
}
