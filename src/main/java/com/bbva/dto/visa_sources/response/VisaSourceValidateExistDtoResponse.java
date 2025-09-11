package com.bbva.dto.visa_sources.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VisaSourceValidateExistDtoResponse {
    private boolean multipleValidation;
    private boolean validated;
    private String replacementId;
}
