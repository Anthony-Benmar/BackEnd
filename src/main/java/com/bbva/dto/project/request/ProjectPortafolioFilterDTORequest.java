package com.bbva.dto.project.request;

import com.bbva.dto.PaginationMasterDtoRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectPortafolioFilterDTORequest extends PaginationMasterDtoRequest {
    private Integer projectId;
    private String isRegulatory;
    private String domainType;
    private Boolean withSources;
}
