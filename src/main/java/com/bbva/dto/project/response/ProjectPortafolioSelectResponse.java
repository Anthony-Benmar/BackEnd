package com.bbva.dto.project.response;

import com.bbva.dto.issueticket.request.WorkOrderDetailDtoRequest;
import com.bbva.dto.map_dependency.response.MapDependencyListByProjectResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ProjectPortafolioSelectResponse {
    private Integer projectId;
    private String projectName;
    private String projectDesc;
    private String sdatoolId;
    private String portafolioCode;
    private Integer scheduleId;
    private Integer projectType;
    private Integer sponsorId;
    private String sponsorName;
    private Integer productOwnerId;
    private String productOwnerName;
    private Boolean regulatoryProjectBoolean;
    private Integer projectDomainType;
    private String ruleAssociatedLink;
    private List<MapDependencyListByProjectResponse> process;
    private String periodId;
    private int statusType;
}
