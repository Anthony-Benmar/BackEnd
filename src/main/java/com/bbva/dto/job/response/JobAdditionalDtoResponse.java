package com.bbva.dto.job.response;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.sql.Timestamp;

@Setter
@Getter
public class JobAdditionalDtoResponse {
    private Integer jobId;
    private Integer createdProjectId;
    private String createdDevEmail;
    private Integer monitoringProjectId;
    private String monitoringDevEmail;
    private Integer reclassificationType;
    private Integer criticalRouteType;
    private String jobFunctionalDesc;
    private String createAuditUser;
    private Timestamp createAuditDate;
    @Nullable
    private Timestamp updateAuditDate;
}
