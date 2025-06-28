package com.bbva.dto.efectivity_base.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class EfectivityBasePaginatedResponseDTO {
    private Integer totalCount;
    private List<EfectivityBaseDataDtoResponse> data;
}