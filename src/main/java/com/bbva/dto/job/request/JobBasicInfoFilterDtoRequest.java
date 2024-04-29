package com.bbva.dto.job.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JobBasicInfoFilterDtoRequest {
    public Integer page;
    public Integer records_amount;

    public Integer domainId;
    public Integer projectId;
    public String jobDataprocFolderName;
    public Integer classificationType;
    public Integer invetoriedType;
}
