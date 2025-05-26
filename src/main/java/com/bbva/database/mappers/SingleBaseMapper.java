package com.bbva.database.mappers;

import com.bbva.dto.single_base.response.SingleBaseResponseDTO;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SingleBaseMapper {

    // Llamada al procedimiento GET_BASE_UNICA con un parámetro 'table_name'
    @Select("CALL GET_BASE_UNICA()")  // El procedimiento GET_BASE_UNICA no necesita el parámetro aquí, según el ejemplo anterior
    @Result(property = "id", column = "id")
    @Result(property = "folio", column = "folio")
    @Result(property = "projectName", column = "project_name")
    @Result(property = "ucSourceName", column = "uc_source_name")
    @Result(property = "ucSourceDesc", column = "uc_source_desc")
    @Result(property = "registeredFolioDate", column = "registered_folio_date")
    @Result(property = "statusFolioType", column = "status_folio_type")
    @Result(property = "analystProjectId", column = "analyst_project_id")
    @Result(property = "analystCaId", column = "analyst_ca_id")
    @Result(property = "resolutionSourceType", column = "resolution_source_type")
    @Result(property = "resolutionSourceDate", column = "resolution_source_date")
    @Result(property = "reusedFolioCode", column = "reused_folio_code")
    @Result(property = "resolutionCommentDesc", column = "resolution_comment_desc")
    @Result(property = "folioType", column = "folio_type")
    @Result(property = "oldSourceId", column = "old_source_id")
    @Result(property = "ucFinalistDesc", column = "uc_finalist_desc")
    @Result(property = "catalogId", column = "catalog_id")
    List<SingleBaseResponseDTO> getBaseUnicaData();
}
