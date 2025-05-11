package com.bbva.database.mappers;

import com.bbva.dto.exception.response.ExceptionEntityResponseDTO;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ExceptionsMapper {
    @Select("CALL GET_EXCEPTIONS_WITH_SOURCE()")
    @Result(property = "sourceId", column = "source_id")
    @Result(property = "tdsDescription", column = "tds_description")
    @Result(property = "tdsSource", column = "tds_source")
    @Result(property = "requestingProject", column = "requesting_project")
    @Result(property = "requestStatus", column = "request_status")
    @Result(property = "registrationDate", column = "registration_date")
    @Result(property = "quarterYearSprint", column = "quarter_year_sprint")
    @Result(property = "shutdownCommitmentDate", column = "shutdown_commitment_date")
    @Result(property = "shutdownCommitmentStatus", column = "shutdown_commitment_status")
    @Result(property = "shutdownProject", column = "shutdown_project")
    List<ExceptionEntityResponseDTO> getExceptionsWithSource();
}




