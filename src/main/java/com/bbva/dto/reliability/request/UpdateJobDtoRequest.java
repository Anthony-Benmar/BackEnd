package com.bbva.dto.reliability.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateJobDtoRequest {
    private String actorRole;
    private String pack;
    private String jobName;
    private String componentName;
    private Integer frequencyId;
    private String inputPaths;
    private String outputPath;
    private Integer jobTypeId;
    private Integer useCaseId;
    private String isCritical;
    private Integer domainId;
    private String bitBucketUrl;
    private String responsible;
    private Integer jobPhaseId;
    private Integer originTypeId;
    private String exception;
    private String comments;
}

