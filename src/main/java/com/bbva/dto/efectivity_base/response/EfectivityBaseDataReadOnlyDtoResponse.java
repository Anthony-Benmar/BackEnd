package com.bbva.dto.efectivity_base.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EfectivityBaseDataReadOnlyDtoResponse {
    private String id;
    private String ticketCode;
    private String sprintDate;
    private String sdatoolProject;
    private String sdatoolFinalProject;
    private String folio;
    private String tdsDescription;
    private String registerDate;
    private String analystAmbassador;
    private String registrationResponsible;
    private String buildObservations;
    private String registrationObservations;
    private String sourceTable;
}