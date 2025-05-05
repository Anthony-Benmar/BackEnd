package com.bbva.dto.use_case.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UseCaseInputsFilterDtoRequest {
    private Integer page;
    private Integer records_amount;

    private String domainName;
    private String projectName;
}
