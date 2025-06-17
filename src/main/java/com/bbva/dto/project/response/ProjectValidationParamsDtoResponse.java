package com.bbva.dto.project.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProjectValidationParamsDtoResponse {
    private String type;
    private String message;
}
