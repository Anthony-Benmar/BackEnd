package com.bbva.dto.visa_sources.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisaSourcesPaginationDtoResponse {
    private Integer totalCount;
    private List<VisaSourcesDataDtoResponse> data;
}
