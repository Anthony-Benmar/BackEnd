package com.bbva.dto.project.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectInfoFilterByDomainDtoRequest {
    public Integer page;
    public Integer records_amount;
    public Integer domainId;
    public Integer projectId;
}
