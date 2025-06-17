package com.bbva.dto.exception_base.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionBaseDataDtoResponse {
    private String id;
    private String sourceId;
    private String tdsDescription;
    private String tdsSource;
    private String requestingProject;
    private String approvalResponsible;
    private String requestStatus;
    private String registrationDate;
    private String quarterYearSprint;
    private String shutdownCommitmentDate;
    private String shutdownCommitmentStatus;
    private String shutdownProject;
}