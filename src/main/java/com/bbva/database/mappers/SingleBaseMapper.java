package com.bbva.database.mappers;

import com.bbva.dto.single_base.response.SingleBaseDataDtoResponse;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SingleBaseMapper {

    @Select("CALL GET_BASE_UNICA(#{limit}, #{offset}, #{projectName}, #{tipoFolio}, #{folio}, #{registeredFolioDate})")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "folio", column = "folio"),
            @Result(property = "projectName", column = "project_name"),
            @Result(property = "ucSourceName", column = "uc_source_name"),
            @Result(property = "ucSourceDesc", column = "uc_source_desc"),
            @Result(property = "registeredFolioDate", column = "registered_folio_date"),
            @Result(property = "statusFolioType", column = "status_folio_type"),
            @Result(property = "analystProjectId", column = "analyst_project_id"),
            @Result(property = "analystCaId", column = "analyst_ca_id"),
            @Result(property = "resolutionSourceType", column = "resolution_source_type"),
            @Result(property = "resolutionSourceDate", column = "resolution_source_date"),
            @Result(property = "reusedFolioCode", column = "reused_folio_code"),
            @Result(property = "resolutionCommentDesc", column = "resolution_comment_desc"),
            @Result(property = "folioType", column = "folio_type"),
            @Result(property = "oldSourceId", column = "old_source_id"),
            @Result(property = "ucFinalistDesc", column = "uc_finalist_desc"),
            @Result(property = "catalogId", column = "catalog_id")
    })
    List<SingleBaseDataDtoResponse> getBaseUnicaDataWithFilters(
            @Param("limit") int limit,
            @Param("offset") int offset,
            @Param("projectName") String projectName,
            @Param("tipoFolio") String tipoFolio,
            @Param("folio") String folio,
            @Param("registeredFolioDate") String registeredFolioDate // <-- Nuevo parámetro
    );

    @Select("CALL GET_BASE_UNICA_TOTAL(#{projectName}, #{tipoFolio}, #{folio}, #{registeredFolioDate})")
    int getBaseUnicaTotalCountWithFilters(
            @Param("projectName") String projectName,
            @Param("tipoFolio") String tipoFolio,
            @Param("folio") String folio,
            @Param("registeredFolioDate") String registeredFolioDate // <-- Nuevo parámetro
    );

    // Métodos para combos
    @Select("SELECT DISTINCT folio FROM folios WHERE folio IS NOT NULL")
    List<String> getDistinctFolios();

    @Select("SELECT DISTINCT project_name FROM folios WHERE project_name IS NOT NULL")
    List<String> getDistinctProjectNames();

    @Select("SELECT DISTINCT registered_folio_date FROM folios WHERE registered_folio_date IS NOT NULL")
    List<java.sql.Date> getDistinctRegisteredFolioDates();

    @Select("SELECT DISTINCT status_folio_type FROM folios WHERE status_folio_type IS NOT NULL")
    List<String> getDistinctStatusFolioTypes();

    @Select("SELECT DISTINCT folio_type FROM folios WHERE folio_type IS NOT NULL")
    List<String> getDistinctFolioTypes();
}