package com.bbva.database.mappers;

import com.bbva.dto.efectivity_base.response.EfectivityBaseDataDtoResponse;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface EfectivityBaseMapper {

    @Select("CALL GET_BASE_EFECTIVITY(#{limit}, #{offset}, #{sdatoolProject}, #{sprintDate}, #{registerDate}, #{efficiency})")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "ticketCode", column = "ticket_code"),
            @Result(property = "sprintDate", column = "sprint_date"),
            @Result(property = "sdatoolProject", column = "sdatool_project"),
            @Result(property = "sdatoolFinalProject", column = "sdatool_final_project"),
            @Result(property = "folio", column = "folio"),
            @Result(property = "tdsDescription", column = "tds_description"),
            @Result(property = "registerDate", column = "register_date"),
            @Result(property = "analystAmbassador", column = "analyst_ambassador"),
            @Result(property = "registrationResponsible", column = "registration_responsible"),
            @Result(property = "buildObservations", column = "build_observations"),
            @Result(property = "registrationObservations", column = "registration_observations"),
            @Result(property = "sourceTable", column = "source_table")
    })
    List<EfectivityBaseDataDtoResponse> getBaseEfectivityDataWithFilters(
            @Param("limit") int limit,
            @Param("offset") int offset,
            @Param("sdatoolProject") String sdatoolProject,
            @Param("sprintDate") String sprintDate,
            @Param("registerDate") String registerDate,
            @Param("efficiency") String efficiency
    );

    @Select("CALL GET_BASE_EFECTIVITY_TOTAL(#{sdatoolProject}, #{sprintDate}, #{registerDate}, #{efficiency})")
    int getBaseEfectivityTotalCountWithFilters(
            @Param("sdatoolProject") String sdatoolProject,
            @Param("sprintDate") String sprintDate,
            @Param("registerDate") String registerDate,
            @Param("efficiency") String efficiency
    );

    // Métodos para combos
    @Select("SELECT DISTINCT sdatool_project FROM visa WHERE sdatool_project IS NOT NULL " +
            "UNION SELECT DISTINCT sdatool_project FROM rulings WHERE sdatool_project IS NOT NULL " +
            "UNION SELECT DISTINCT sdatool_project FROM reuse_analysis WHERE sdatool_project IS NOT NULL")
    List<String> getDistinctSdatoolProjects();

    @Select("SELECT DISTINCT sprint_date FROM visa WHERE sprint_date IS NOT NULL " +
            "UNION SELECT DISTINCT sprint_date FROM rulings WHERE sprint_date IS NOT NULL")
    List<String> getDistinctSprintDates();

    @Select("SELECT DISTINCT register_date FROM visa WHERE register_date IS NOT NULL")
    List<java.sql.Date> getDistinctRegisterDates();

    @Select("SELECT DISTINCT efficiency FROM visa WHERE efficiency IS NOT NULL " +
            "UNION SELECT DISTINCT registration_observations FROM rulings WHERE registration_observations IS NOT NULL " +
            "UNION SELECT DISTINCT registration_observations FROM reuse_analysis WHERE registration_observations IS NOT NULL")
    List<String> getDistinctEfficiencies();
}