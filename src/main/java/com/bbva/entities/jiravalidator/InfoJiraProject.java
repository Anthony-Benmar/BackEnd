package com.bbva.entities.jiravalidator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class InfoJiraProject {
    
    private String sdatoolId;
    private String projectName;
    private String projectDesc;
    private String participantEmail;
    private String projectRolType;
    private String projectRolName;
    private String teamBackLogId;
    private String teamBackLogName;
}
