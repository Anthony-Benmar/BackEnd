package com.bbva.dto.project.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectInfoFilterRequest {
    public Integer page;
    public Integer records_amount;
    public Integer projectId;
    public String sdatoolId;
    public String sdatoolIdOrProjectName;
    public Integer domainId;
    public Integer statusType;
    public Integer projectType;
    public Integer wowType;
}
