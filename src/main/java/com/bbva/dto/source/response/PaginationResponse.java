package com.bbva.dto.source.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginationResponse {
    private Integer count;
    private Integer pages_amount;
    List<PaginationDataDtoResponse> data;
}
