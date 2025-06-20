package com.bbva.dto.reliability.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginationReliabilityPackResponse {
    private Integer count;
    private Integer pagesAmount;
    private List<ReliabilityPacksDtoResponse> data;
}
