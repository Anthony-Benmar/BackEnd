package com.bbva.entities.feature;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
public class JiraFeatureEntity {
    public Integer featureId;
    public String featureKey;
    public String featureName;
    public String sdatoolId;
    public String teamBacklog;
    public Integer jiraProjectId;
    public String jiraProjectName;
}
