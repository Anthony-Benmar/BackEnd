package com.bbva.dto.reliability.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ExecutionValidationAllDtoResponse {
    private String jobName;
    private String validation;
}
