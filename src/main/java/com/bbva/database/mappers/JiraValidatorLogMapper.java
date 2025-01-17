package com.bbva.database.mappers;

import com.bbva.entities.jiravalidator.JiraValidatorLogEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

public interface JiraValidatorLogMapper {

    String SQL_QUERY_INSERT_LOG = "INSERT INTO jira_validator_log (fecha, usuario, nombre, ticket, " +
        "regla_1, regla_2, regla_3, regla_4, regla_5, regla_6, regla_7, regla_8, regla_9, regla_10, " +
        "regla_11, regla_12, regla_13, regla_14, regla_15, regla_16, regla_17, regla_18, regla_19, regla_20, " +
        "regla_21, regla_22, regla_23, regla_24, regla_25, regla_26, regla_27, regla_28, regla_29, regla_30) " +
        "VALUES (#{fecha}, #{usuario}, #{nombre}, #{ticket}, " +
            "#{regla1}, #{regla2}, #{regla3}, #{regla4}, #{regla5}, #{regla6}, #{regla7}, #{regla8}, #{regla9}, #{regla10}, " +
            "#{regla11}, #{regla12}, #{regla13}, #{regla14}, #{regla15}, #{regla16}, #{regla17}, #{regla18}, #{regla19}, #{regla20}, " +
            "#{regla21}, #{regla22}, #{regla23}, #{regla24}, #{regla25}, #{regla26}, #{regla27}, #{regla28}, #{regla29}, #{regla30} )";

    @Insert(SQL_QUERY_INSERT_LOG)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertJiraValidatorLog(JiraValidatorLogEntity log);
}
