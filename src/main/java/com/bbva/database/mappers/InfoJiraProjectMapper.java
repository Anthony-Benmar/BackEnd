package com.bbva.database.mappers;

import com.bbva.entities.jiravalidator.InfoJiraProject;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface InfoJiraProjectMapper {
    
    String SQL_QUERY_ACTION = "select pi.sdatool_id as sdatoolId, \n" +
            "    pi.project_name as projectName, \n" +
            "    pi.project_desc as projectDesc, \n" +
            "    pp.participant_email as participantEmail, \n" +
            "    pp.project_rol_type as projectRolType,\n" +
            "    cat.element_name as projectRolName,\n" +
            "    jtb.`Team Backlog ID` as teamBackLogId,\n" +
            "    jtb.`Team Backlog Name` as teamBackLogName \n" +
            "from project_info pi\n" +
            "left join project_participant pp \n" +
            "\ton pi.project_id = pp.project_id\n" +
            "left join catalog cat\n" +
            "\ton cat.element_id = pp.project_rol_type\n" +
            "\tand cat.catalog_id = 1037\n" +
            "left join jira_board jb\n" +
            "\ton pp.project_id  = jb.project_id \n" +
            "left join jira_team_backlog jtb\n" +
            "\ton jb.board_jira_id = jtb.`Team Backlog ID` \n" +
            "left join calendar_pi cp\n" +
            "\ton pp.pi_id = cp.pi_id \n" +
            "\tand CURDATE() BETWEEN cp.start_date and cp.end_date \n" +
            "where DATE_FORMAT(CURDATE(), '%Y%m') BETWEEN pi.start_pi_id and pi.end_pi_id";

    @Select(SQL_QUERY_ACTION)
    List<InfoJiraProject> list();

    String SQL_CURRENT_Q ="SELECT pi_id FROM calendar_pi WHERE CURDATE() BETWEEN start_date AND end_date";
    @Select(SQL_CURRENT_Q)
    String currentQ();

}
