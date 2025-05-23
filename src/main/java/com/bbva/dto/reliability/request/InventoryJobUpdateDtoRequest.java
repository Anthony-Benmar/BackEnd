package com.bbva.dto.reliability.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InventoryJobUpdateDtoRequest {
    private String jobName;
    private String componentName;
    private int frequencyId;
    private String inputPaths;
    private String outputPath;
    private int jobTypeId;
    private int useCaseId;
    private String isCritical;
    private int domainId;
}