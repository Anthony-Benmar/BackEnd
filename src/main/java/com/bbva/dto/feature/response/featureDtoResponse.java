package com.bbva.dto.feature.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class featureDtoResponse {
    public Integer featureId;
    public String featureKey;
    public String featureName;
    public String sdatoolId;
    public String teamBacklog;
    public Integer jiraProjectId;
    public String jiraProjectName;
}
