package com.bbva.database.mappers;

import com.bbva.entities.jiravalidator.JiraValidatorLogEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

public interface JiraValidatorLogMapper {

    String SQL_QUERY_INSERT_LOG = "CALL InsertJiraValidatorLog(#{fecha}, #{usuario}, #{nombre}, #{ticket}, " +
            "#{regla1}, #{regla2}, #{regla3}, #{regla4}, #{regla5}, #{regla6}, #{regla7}, #{regla8}, #{regla9}, #{regla10}, " +
            "#{regla11}, #{regla12}, #{regla13}, #{regla14}, #{regla15}, #{regla16}, #{regla17}, #{regla18}, #{regla19}, #{regla20}, " +
            "#{regla21}, #{regla22}, #{regla23}, #{regla24}, #{regla25}, #{regla26}, #{regla27}, #{regla28}, #{regla29}, #{regla30}," +
            "#{regla31}, #{regla32}, #{regla33}, #{regla34}, #{regla35}, #{regla36}, #{regla37}, #{regla38}, #{regla39}, #{regla40} )";

    @Insert(SQL_QUERY_INSERT_LOG)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertJiraValidatorLog(JiraValidatorLogEntity log);
}
