package com.bbva.dto.use_case.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UseCaseInputsDtoResponse {
    private String domainName;
    private String useCaseName;
    private String useCaseDescription;
    private Integer projectCount;
    private String projects;
}