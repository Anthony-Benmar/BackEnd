package com.bbva.entities.mesh;


import lombok.*;

import java.sql.Timestamp;
import java.util.Date;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class JobBasicInfoEntity {
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
