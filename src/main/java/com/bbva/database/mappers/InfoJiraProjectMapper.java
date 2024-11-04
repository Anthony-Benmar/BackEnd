package com.bbva.database.mappers;

import com.bbva.entities.jiravalidator.InfoJiraProject;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface InfoJiraProjectMapper {
    
    String SQL_QUERY_ACTION = "{call GetCurrentInfoJiraProject()}";

    @Select(SQL_QUERY_ACTION)
    List<InfoJiraProject> list();

    String SQL_CURRENT_Q ="SELECT pi_large_name FROM calendar_pi WHERE CURDATE() BETWEEN start_date AND end_date";
    @Select(SQL_CURRENT_Q)
    String currentQ();

}
