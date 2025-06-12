package com.bbva.dto.reliability.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ExecutionValidationInputsDtoRequest {
    private String jobName;
    private String frequency;
    private String componentName;
    private String comments;
    private String jobType;
    private String originType;
    private String phaseType;
    private String inputPaths;
    private String outputPaths;
    private String responsible;
    private String status;
    private String jsonName;
}
