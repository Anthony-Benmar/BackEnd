package com.bbva.dto.use_case.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrInsertUseCaseDtoRequest {
    private Integer useCaseId;
    private String useCaseName;
    private String useCaseDescription;
    private Integer domainId;
    private String action;
    private String userId;
}
