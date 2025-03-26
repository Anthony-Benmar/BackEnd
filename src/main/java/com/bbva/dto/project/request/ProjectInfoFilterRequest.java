package com.bbva.dto.project.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectInfoFilterRequest {
    private Integer page;
    private Integer records_amount;
    private Integer projectId;
    private String sdatoolIdOrProjectName;
    private String domainId;
    private String statusType;
    private String projectType;
    private String wowType;
    private String startQ;
    private String endQ;
}
