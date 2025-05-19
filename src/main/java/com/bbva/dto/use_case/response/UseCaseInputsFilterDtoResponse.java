package com.bbva.dto.use_case.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class UseCaseInputsFilterDtoResponse {
    private Integer count;
    private Integer pagesAmount;
    private List<UseCaseInputsDtoResponse> data;
}