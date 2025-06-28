package com.bbva.dto.source_with_parameter.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SourceWithParameterPaginatedResponseDTO {
    private Integer totalCount;
    private List<SourceWithParameterDataDtoResponse> data;
}
