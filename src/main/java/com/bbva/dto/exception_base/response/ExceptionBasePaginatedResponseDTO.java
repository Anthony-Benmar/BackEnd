package com.bbva.dto.exception_base.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ExceptionBasePaginatedResponseDTO {
    private Integer totalCount;
    private List<ExceptionBaseDataDtoResponse> data;
}