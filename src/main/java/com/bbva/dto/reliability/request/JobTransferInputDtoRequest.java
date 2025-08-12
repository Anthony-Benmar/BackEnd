package com.bbva.dto.reliability.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JobTransferInputDtoRequest {
    private Integer jobTypeId;
    private String jobName;
    private String componentName;
    private Integer frequencyId;
    private String bitbucketUrl;
    private String inputPaths;
    private String outputPath;
    private String responsible;
    private String comments;
    private Integer jobPhaseId;
    private Integer originTypeId;
    private Integer useCaseId;
    private String isCritical;
    private Integer domainId;
    private String pack;
    private Integer statusId;
    private String exception;
}
