package com.bbva.database.mappers;

import com.bbva.dto.efectivity.response.EfectivityEntityResponseDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface EfectivityMapper {
    @Select("CALL GET_BASE_OF_EFECTIVITY(#{table_name})")
    @Results({
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
            @Result(property = "id", column = "id"),
    })
    List<EfectivityEntityResponseDTO> getEfectivityWithSource(@Param("table_name") String tableName);
}
