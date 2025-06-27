package com.bbva.dto.exception_base.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionBasePaginationDtoRequest {
    private Integer limit;
    private Integer offset;
    private String requestingProject;
    private String approvalResponsible;
    private String registrationDate;
    private String quarterYearSprint;
}