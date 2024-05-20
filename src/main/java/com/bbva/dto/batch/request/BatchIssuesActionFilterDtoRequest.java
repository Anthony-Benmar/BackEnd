package com.bbva.dto.batch.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BatchIssuesActionFilterDtoRequest {
    private Integer page;
    private Integer recordsAmount;

    private String domainId;
    private Integer projectId;
    private String jobName;
    private String folderName;
    private Integer statusType;
}
