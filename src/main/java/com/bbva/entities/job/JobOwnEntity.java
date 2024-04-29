package com.bbva.entities.job;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class JobOwnEntity {

    private Integer jobId;
    //Quien creo el job
    private Integer createdProjectId;
    private String createdDevEmail;
    //Quien es responsable del monitoreo
    private Integer monitoringProjectId;
    private String monitoringDevEmail;
    //Datos Adicionales
    private Integer reclassificationType;
    private Integer criticalRouteType;
    private String jobFunctionalDesc;
    //Campos de auditoria
    private Date createAuditDate;
    private String createAuditUser;
    private Date updateAuditDate;
    private String updateAuditUser;
}
