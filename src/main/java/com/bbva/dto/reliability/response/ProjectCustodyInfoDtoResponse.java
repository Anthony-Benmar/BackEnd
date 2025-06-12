package com.bbva.dto.reliability.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectCustodyInfoDtoResponse {
    private Integer useCaseId;
    private String useCase;
    private Integer domainId;
    private String pack;
    private String domainName;
    private Integer productOwnerUserId;
    private String productOwner;
}
