package com.bbva.dto.government.request;

import com.bbva.dto.PaginationMasterDtoRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterSourceRequestDTO extends PaginationMasterDtoRequest {
    private Integer projectId;
    private String domainType;
}
