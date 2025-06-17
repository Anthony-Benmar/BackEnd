package com.bbva.dto.project.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectValidationParamsDtoResponse {
    private String type;
    private String message;
}
