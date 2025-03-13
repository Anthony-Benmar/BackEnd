package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneradorDocumentosMallasRequest {
    private int projectId;
    private String projectDescription;
    private String sdatool;
    private String userName;
    private String employeeId;
    private String name;
    private String token;
    private DataDocumentosMallas dataDocumentosMallas;
}
