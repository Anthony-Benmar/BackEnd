package com.bbva.dto.reliability.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectCustodyInfoDtoResponse {
    private String useCase;
    private String pack;
    private String domainName;
    private String productOwner;
}
