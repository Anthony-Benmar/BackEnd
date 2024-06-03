package com.bbva.dto.jira.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JiraValidateDtoResponse {
    private String idRule;
    private String description;
    private String mandatory;
    private String attention;
    private String detail;
}
