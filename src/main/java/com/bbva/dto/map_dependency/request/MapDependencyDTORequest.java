package com.bbva.dto.map_dependency.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MapDependencyDTORequest {
    private Integer mapDependencyId;
    private Integer useCaseId;
    private Integer keyDataProcessType;
    private String processName;
    private Integer sloOwnerId;
    private String arisCode;
    private String dependencyMapLink;
}
