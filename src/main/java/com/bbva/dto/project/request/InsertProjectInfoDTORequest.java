package com.bbva.dto.project.request;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.List;

@Getter
@Setter
public class InsertProjectInfoDTORequest {
    public Integer projectId;
    public String sdatoolId;
    public String projectName;
    public String projectDesc;
    public String portafolioCode;
    public String domainId;
    public Integer ttvType;
    public Integer regulatoryType;
    public Integer projectType;
    @Nullable
    public Integer categoryType;
    @Nullable
    public Integer classificationType;
    public Integer startPiId;
    public Integer endPiId;
    public Integer finalStartPiId;
    public Integer finalEndPiId;
    public Integer wowType;
    public Integer countryPriorityType;
    public String createAuditUser;
    public List<InsertProjectParticipantDTO> participants;
    public List<InsertProjectDocumentDTO> documents;
    public Integer statusType;
    private Integer useCaseId;
}
