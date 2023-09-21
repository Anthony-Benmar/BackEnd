package com.bbva.dto.project.request;

import com.bbva.dto.map_dependency.request.MapDependencyDTORequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ProjectPortafolioDTORequest {
    private Integer projectId;
    private String projectName;
    private String projectDesc;
    private int statusType;
    private String sdatoolId;
    private String portafolioCode;
    private Integer scheduleId;
    private Integer projectType;
    private Integer sponsorId;
    private Integer productOwnerId;
    private Boolean regulatoryProjectBoolean;
    private Integer projectDomainType;
    private String ruleAssociatedLink;
    private String periodId;
    private List<MapDependencyDTORequest> process;
}
