package com.bbva.dto.project.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectByDomainIdDTO {
    private int projectId;
    private String projectName;
    private int domainId;
}
