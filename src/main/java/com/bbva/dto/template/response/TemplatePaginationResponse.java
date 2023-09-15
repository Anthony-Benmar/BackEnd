package com.bbva.dto.template.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TemplatePaginationResponse {
    private Integer templateId;
    private String template;
    private String labelOne;
    private Integer orden;
}
