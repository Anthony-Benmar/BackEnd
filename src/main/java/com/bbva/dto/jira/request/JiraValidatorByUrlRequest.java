package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JiraValidatorByUrlRequest {
    private String urlJira;
    private String userName;
    private String token;

}
