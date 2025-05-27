package com.bbva.dto.reliability.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ExecutionValidationAllDtoResponse {
    private String jobName;
    private String validation;
}
