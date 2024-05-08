package com.bbva.dto.project.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectByDomainIdDTO {
    private String projectId;
    private String sdatoolId;
    private String projectName;
    private String domainId;
}
