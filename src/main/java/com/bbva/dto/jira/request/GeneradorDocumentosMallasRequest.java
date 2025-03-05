package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneradorDocumentosMallasRequest {
    private String urlJira;
    private String userName;
    private String name;
    private String token;
    private DataDocumentosMallas dataDocumentosMallas;
}
