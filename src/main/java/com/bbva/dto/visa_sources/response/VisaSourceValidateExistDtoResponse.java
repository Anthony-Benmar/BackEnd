package com.bbva.dto.visa_sources.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VisaSourceValidateExistDtoResponse {
    private Boolean multipleValidation;
    private Boolean validated;
    private String replacementId;
}
