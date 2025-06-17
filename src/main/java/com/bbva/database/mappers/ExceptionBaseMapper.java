package com.bbva.database.mappers;

import com.bbva.dto.exception_base.response.ExceptionBaseDataDtoResponse;
import com.bbva.dto.exception_base.response.ExceptionBaseReadOnlyDtoResponse;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ExceptionBaseMapper {

    @Select("CALL GET_BASE_EXCEPTIONS(#{limit}, #{offset}, #{requestingProject}, #{approvalResponsible}, #{registrationDate}, #{quarterYearSprint})")
    @Results({
            @Result(property = "id", column = "ID"),
            @Result(property = "sourceId", column = "source_id"),
            @Result(property = "tdsDescription", column = "tds_description"),
            @Result(property = "tdsSource", column = "tds_source"),
            @Result(property = "requestingProject", column = "requesting_project"),
            @Result(property = "approvalResponsible", column = "approval_responsible"),
            @Result(property = "requestStatus", column = "request_status"),
            @Result(property = "registrationDate", column = "registration_date"),
            @Result(property = "quarterYearSprint", column = "quarter_year_sprint"),
            @Result(property = "shutdownCommitmentDate", column = "shutdown_commitment_date"),
            @Result(property = "shutdownCommitmentStatus", column = "shutdown_commitment_status"),
            @Result(property = "shutdownProject", column = "shutdown_project")
    })
    List<ExceptionBaseDataDtoResponse> getExceptionsDataWithFilters(
            @Param("limit") int limit,
            @Param("offset") int offset,
            @Param("requestingProject") String requestingProject,
            @Param("approvalResponsible") String approvalResponsible,
            @Param("registrationDate") String registrationDate,
            @Param("quarterYearSprint") String quarterYearSprint
    );

    @Select("CALL GET_BASE_EXCEPTIONS_TOTAL(#{requestingProject}, #{approvalResponsible}, #{registrationDate}, #{quarterYearSprint})")
    int getExceptionsTotalCountWithFilters(
            @Param("requestingProject") String requestingProject,
            @Param("approvalResponsible") String approvalResponsible,
            @Param("registrationDate") String registrationDate,
            @Param("quarterYearSprint") String quarterYearSprint
    );

    // Métodos para combos
    @Select("CALL GET_DISTINCT_REQUESTING_PROJECTS()")
    List<String> getDistinctRequestingProjects();

    @Select("CALL GET_DISTINCT_APPROVAL_RESPONSIBLES()")
    List<String> getDistinctApprovalResponsibles();

    @Select("CALL GET_DISTINCT_REGISTRATION_DATES()")
    List<String> getDistinctRegistrationDates();

    @Select("CALL GET_DISTINCT_QUARTER_YEAR_SPRINTS()")
    List<String> getDistinctQuarterYearSprints();

    // Detalle por ID
    @Select("SELECT * FROM exceptions_new WHERE ID = #{exceptionId}")
    @Results({
            @Result(property = "id", column = "ID"),
            @Result(property = "sourceId", column = "source_id"),
            @Result(property = "tdsDescription", column = "tds_description"),
            @Result(property = "tdsSource", column = "tds_source"),
            @Result(property = "requestingProject", column = "requesting_project"),
            @Result(property = "approvalResponsible", column = "approval_responsible"),
            @Result(property = "requestStatus", column = "request_status"),
            @Result(property = "registrationDate", column = "registration_date"),
            @Result(property = "quarterYearSprint", column = "quarter_year_sprint"),
            @Result(property = "shutdownCommitmentDate", column = "shutdown_commitment_date"),
            @Result(property = "shutdownCommitmentStatus", column = "shutdown_commitment_status"),
            @Result(property = "shutdownProject", column = "shutdown_project")
    })
    ExceptionBaseDataDtoResponse getExceptionById(@Param("exceptionId") String exceptionId);
}