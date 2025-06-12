package com.bbva.database.mappers;

import com.bbva.entities.feature.JiraFeatureEntity2;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface FeatureMapper {
    @Insert("INSERT INTO jira_features(feature_key, feature_name, jira_project_id, jira_project_name, sdatool_id, team_backlog) " +
            "VALUES (#{featureKey}, #{featureName}, #{jiraProjectId}, #{jiraProjectName}, #{sdatoolId}, #{teamBacklog})")
    @Options(useGeneratedKeys = true, keyProperty = "featureId", keyColumn = "feature_id")
    void insertFeature(JiraFeatureEntity2 feature); //Revisar luego

    @Select("SELECT * FROM jira_features WHERE feature_key = #{featureKey}")
    JiraFeatureEntity2 getFeatureByKey(@Param("featureKey") String featureKey);
}
