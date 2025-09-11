package com.bbva.dto.visa_sources.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApproveVisaSourceDtoRequest {
    private Integer id;
    private String sourceId;
    private Boolean isMinorChange;
    private Boolean isSubstitution;
}