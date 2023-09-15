package com.bbva.dto.map_dependency.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class MapDependencyListByProjectResponse {

    private Integer mapDependencyId;
    private Integer useCaseId;
    private Integer keyDataProcessType;
    private String keyDataProcessName;
    private Integer processNameType;
    private String processName;
    private Integer sloOwnerId;
    private String sloOwnerName;
    private String sloOwnerCode;
    private String arisCode;
    private String dependencyMapLink;
}
