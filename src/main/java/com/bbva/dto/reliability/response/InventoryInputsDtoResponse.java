package com.bbva.dto.reliability.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InventoryInputsDtoResponse {
    private String domainName;
    private String useCase;
    private String jobName;
    private String componentName;
    private String jobType;
    private String isCritical;
    //private Boolean isCritical;
    private String frequency;
    private String inputPaths;
    private String[] inputPathsArray;
    private String outputPath;
    private String jobPhase;
    private Integer domainId;
    private Integer useCaseId;
    private Integer frequencyId;
    private Integer jobTypeId;
    private String bitBucketUrl;
    private String pack;
}
