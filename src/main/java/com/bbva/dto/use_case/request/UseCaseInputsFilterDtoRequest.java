package com.bbva.dto.use_case.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UseCaseInputsFilterDtoRequest {
    private Integer page;
    private Integer recordsAmount;
    private String critical;
    private String domainName;
    private String projectName;
}