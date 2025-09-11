package com.bbva.dto.visa_sources.response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisaSourcesDataDtoResponse {
    private Integer id;
    private String sourceType;
    private String userStory;
    private String quarter;
    private String registerDate;
    private String sdatoolProject;
    private String sdatoolFinal;
    private String functionalAnalist;
    private String domain;
    private String folio;
    private String tdsProposalName;
    private String tdsDescription;
    private String tdsProof;
    private String tdsProof2;
    private String originSource;
    private String originType;
    private String ownerModel;
    private String uuaaRaw;
    private String uuaaMaster;
    private String criticalTable;
    private String functionalChecklist;
    private String structure;
    private String status;
}
