package com.bbva.dto.jira.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Locale;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JiraValidatorByUrlRequest {
    private String urlJira;
    private String userName;
    private String name;
    private String token;
    private String timestamp;
}
