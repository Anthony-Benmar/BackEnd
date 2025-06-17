package com.bbva.entities.feature;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
public class JiraFeatureEntity2 {
//    public Integer featureId;
//    public String featureKey;
//    public String featureName;
//    public String sdatoolId;
//    public String teamBacklog;
//    public Integer jiraProjectId;
//    public String jiraProjectName;
//    // agregar campos extra -----
//    public String featureUrl;
//    public String description;
//    public int boardId;
//    public Date createdDate;
//    public String status;

    public int featureId;
    public String featureKey;
    public String featureName;
    public String featureUrl;
    public String description;
    public Integer jiraProjectId;
    public String jiraProjectName;
    public int boardId;
    public String status;
    public Date createdDate;
}
