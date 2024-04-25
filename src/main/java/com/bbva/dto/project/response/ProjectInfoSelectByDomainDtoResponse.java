package com.bbva.dto.project.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectInfoSelectByDomainDtoResponse {
    public Integer projectId;
    public String domainId;
    public String projectName;
}
