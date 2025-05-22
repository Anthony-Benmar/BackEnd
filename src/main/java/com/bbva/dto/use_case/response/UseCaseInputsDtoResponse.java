package com.bbva.dto.use_case.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UseCaseInputsDtoResponse {
    private Integer useCaseId;
    private String domainName;
    private Integer domainId;
    private String useCaseName;
    private String useCaseDescription;
    private Integer projectCount;
    private String projects;
    private Integer deliveredPiId;
    private String piLargeName;
    private Integer critical;
    private String criticalDesc;
    private Integer isRegulatory;
    private String regulatoryDesc;
    private Integer useCaseScope;
    private String useCaseScopeDesc;
    private Integer operativeModel;
    private String operativeModelDesc;
}
