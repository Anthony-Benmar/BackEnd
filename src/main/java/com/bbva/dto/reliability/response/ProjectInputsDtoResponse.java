package com.bbva.dto.reliability.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProjectInputsDtoResponse {
    private String sdatoolId;
    private String projectName;
    private String domainName;
    private String statusTypeDesc;
    private String projectTypeDesc;
    private String wowName;
    private Integer startPiId;
    private Integer finalStartPiId;
    private Integer endPiId;
    private Integer finalEndPiId;
}
