package com.bbva.database.mappers;

import com.bbva.dto.source_with_parameter.request.SourceWithParameterPaginationDtoRequest;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDataDtoResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface SourceWithParameterMapper {
    @Select("CALL GET_SOURCES_WITH_PARAMETER(#{filter.limit}, #{filter.offset}, #{filter.id}, #{filter.tdsSource}, #{filter.uuaaMaster}, #{filter.modelOwner}," +
            " #{filter.status}, #{filter.originType}, #{filter.tdsOpinionDebt}, #{filter.effectivenessDebt})")
            @Result(property = "id", column = "id")
            @Result(property = "tdsDescription", column = "tds_description")
            @Result(property = "tdsSource", column = "tds_source")
            @Result(property = "sourceOrigin", column = "source_origin")
            @Result(property = "originType", column = "origin_type")
            @Result(property = "status", column = "status")
            @Result(property = "replacementId", column = "replacement_id")
            @Result(property = "modelOwner", column = "model_owner")
            @Result(property = "masterRegisteredBoard", column = "master_registered_board")
            @Result(property = "dataLakeLayer", column = "datalake_layer")
            @Result(property = "uuaaRaw", column = "uuaa_raw")
            @Result(property = "uuaaMaster", column = "uuaa_master")
            @Result(property = "tdsOpinionDebt", column = "tds_opinion_debt")
            @Result(property = "debtLevel", column = "debt_level")
            @Result(property = "inheritedSourceId", column = "inherited_source_id")
            @Result(property = "opinionDebtComments", column = "opinion_debt_comments")
            @Result(property = "missingCertification", column = "missing_certification")
            @Result(property = "missingFieldProfiling", column = "missing_field_profiling")
            @Result(property = "incompleteOpinion", column = "incomplete_opinion")
            @Result(property = "pdcoProcessingUse", column = "pdco_processing_use")
            @Result(property = "effectivenessDebt", column = "effectiveness_debt")
            @Result(property = "ingestionType", column = "ingestion_type")
            @Result(property = "ingestionLayer", column = "ingestion_layer")
            @Result(property = "datioDownloadType", column = "datio_download_type")
            @Result(property = "processingInputTableIds", column = "processing_input_table_ids")
            @Result(property = "periodicity", column = "periodicity")
            @Result(property = "periodicityDetail", column = "periodicity_detail")
            @Result(property = "folderUrl", column = "folder_url")
            @Result(property = "typology", column = "typology")
            @Result(property = "criticalTable", column = "critical_table")
            @Result(property = "criticalTableOwner", column = "critical_table_owner")
            @Result(property = "l1t", column = "l1t")
            @Result(property = "hem", column = "hem")
            @Result(property = "his", column = "his")
            @Result(property = "err", column = "err")
            @Result(property = "log", column = "log")
            @Result(property = "mlg", column = "mlg")
            @Result(property = "quality", column = "quality")
            @Result(property = "tag1", column = "tag1")
            @Result(property = "tag2", column = "tag2")
            @Result(property = "tag3", column = "tag3")
            @Result(property = "tag4", column = "tag4")
            @Result(property = "rawPath", column = "raw_path")
    List<SourceWithParameterDataDtoResponse> getSourcesWithParameterWithFilters(@Param("filter") SourceWithParameterPaginationDtoRequest filter);
    @Select("CALL GET_SOURCES_WITH_PARAMETER_TOTAL(#{filter.id}, #{filter.tdsSource}, #{filter.uuaaMaster}, #{filter.modelOwner}, #{filter.status}," +
            " #{filter.originType}, #{filter.tdsOpinionDebt}, #{filter.effectivenessDebt})")
    int getSourcesWithParameterTotalCountWithFilters(
            @Param("filter")SourceWithParameterPaginationDtoRequest filter
            );

    @Select("SELECT DISTINCT status FROM sources WHERE tds_source IS NOT NULL")
    List<String> getStatus();

    @Select("SELECT DISTINCT origin_type FROM sources WHERE origin_type IS NOT NULL")
    List<String> getOriginType();

    @Select("SELECT DISTINCT tds_opinion_debt FROM sources WHERE tds_opinion_debt IS NOT NULL")
    List<String> getTdsOpinionDebt();

    @Select("SELECT DISTINCT effectiveness_debt FROM sources WHERE effectiveness_debt IS NOT NULL")
    List<String> getEffectivenessDebt();

    @Select("select * from sources where id = #{sourceWithParameterId}")
            @Result(property = "id", column = "id")
            @Result(property = "tdsDescription", column = "tds_description")
            @Result(property = "tdsSource", column = "tds_source")
            @Result(property = "sourceOrigin", column = "source_origin")
            @Result(property = "originType", column = "origin_type")
            @Result(property = "status", column = "status")
            @Result(property = "replacementId", column = "replacement_id")
            @Result(property = "modelOwner", column = "model_owner")
            @Result(property = "masterRegisteredBoard", column = "master_registered_board")
            @Result(property = "dataLakeLayer", column = "datalake_layer")
            @Result(property = "uuaaRaw", column = "uuaa_raw")
            @Result(property = "uuaaMaster", column = "uuaa_master")
            @Result(property = "tdsOpinionDebt", column = "tds_opinion_debt")
            @Result(property = "debtLevel", column = "debt_level")
            @Result(property = "inheritedSourceId", column = "inherited_source_id")
            @Result(property = "opinionDebtComments", column = "opinion_debt_comments")
            @Result(property = "missingCertification", column = "missing_certification")
            @Result(property = "missingFieldProfiling", column = "missing_field_profiling")
            @Result(property = "incompleteOpinion", column = "incomplete_opinion")
            @Result(property = "pdcoProcessingUse", column = "pdco_processing_use")
            @Result(property = "effectivenessDebt", column = "effectiveness_debt")
            @Result(property = "ingestionType", column = "ingestion_type")
            @Result(property = "ingestionLayer", column = "ingestion_layer")
            @Result(property = "datioDownloadType", column = "datio_download_type")
            @Result(property = "processingInputTableIds", column = "processing_input_table_ids")
            @Result(property = "periodicity", column = "periodicity")
            @Result(property = "periodicityDetail", column = "periodicity_detail")
            @Result(property = "folderUrl", column = "folder_url")
            @Result(property = "typology", column = "typology")
            @Result(property = "criticalTable", column = "critical_table")
            @Result(property = "criticalTableOwner", column = "critical_table_owner")
            @Result(property = "l1t", column = "l1t")
            @Result(property = "hem", column = "hem")
            @Result(property = "his", column = "his")
            @Result(property = "err", column = "err")
            @Result(property = "log", column = "log")
            @Result(property = "mlg", column = "mlg")
            @Result(property = "quality", column = "quality")
            @Result(property = "tag1", column = "tag1")
            @Result(property = "tag2", column = "tag2")
            @Result(property = "tag3", column = "tag3")
            @Result(property = "tag4", column = "tag4")
            @Result(property = "rawPath", column = "raw_path")
    SourceWithParameterDataDtoResponse getSourceWithParameterById(@Param("sourceWithParameterId") String sourceWithParameterId);

    @Select("SELECT status FROM sources WHERE id = #{sourceId}")
    String getStatusById(@Param("sourceId") String sourceId);

    @Select("SELECT COUNT(1) FROM sources WHERE id = #{replacementId}")
    int countById(@Param("replacementId") String replacementId);

    @Select("SELECT id FROM sources " +
            "WHERE id REGEXP '^[0-9]+(\\.[0-9]+)?$' " +
            "ORDER BY CAST(id AS DECIMAL(20,10)) DESC LIMIT 1")
    String getMaxSourceId();

    @Insert("INSERT INTO sources (" +
            "id, tds_description, tds_source, origin_type, source_origin, status, " +
            "replacement_id, model_owner, tds_comments, " +
            "create_audit_user, create_audit_user_name, create_audit_date) " +
            "VALUES (" +
            "#{dto.id}, #{dto.tdsDescription}, #{dto.tdsSource}, #{dto.originType}, #{dto.sourceOrigin}, #{dto.status}, " +
            "NULL, #{dto.modelOwner}, #{dto.tdsComments}, " +
            "#{dto.userId}, #{dto.userName}, NOW())")
    int insertSource(@Param("dto") SourceWithParameterDataDtoResponse dto);

    @Update("UPDATE sources SET status = 'Reemplazado', replacement_id = #{newReplacementIds} " +
            "WHERE id = #{oldId}")
    void updateReplacementId(@Param("newReplacementIds") String newReplacementIds, @Param("oldId") String oldId);

    @Select("SELECT replacement_id FROM sources WHERE id = #{id}")
    String getReplacementIds(@Param("id") String id);

    @Update("UPDATE sources SET " +
            "tds_description = #{dto.tdsDescription}, tds_source = #{dto.tdsSource}, source_origin = #{dto.sourceOrigin}, " +
            "origin_type = #{dto.originType}, status = #{dto.status}, replacement_id = #{dto.replacementId}, " +
            "model_owner = #{dto.modelOwner}, master_registered_board = #{dto.masterRegisteredBoard}, " +
            "datalake_layer = #{dto.dataLakeLayer}, uuaa_raw = #{dto.uuaaRaw}, uuaa_master = #{dto.uuaaMaster}, " +
            "tds_opinion_debt = #{dto.tdsOpinionDebt}, debt_level = #{dto.debtLevel}, inherited_source_id = #{dto.inheritedSourceId}, " +
            "opinion_debt_comments = #{dto.opinionDebtComments}, missing_certification = #{dto.missingCertification}, " +
            "missing_field_profiling = #{dto.missingFieldProfiling}, incomplete_opinion = #{dto.incompleteOpinion}, " +
            "pdco_processing_use = #{dto.pdcoProcessingUse}, effectiveness_debt = #{dto.effectivenessDebt}, " +
            "ingestion_type = #{dto.ingestionType}, ingestion_layer = #{dto.ingestionLayer}, datio_download_type = #{dto.datioDownloadType}, " +
            "processing_input_table_ids = #{dto.processingInputTableIds}, periodicity = #{dto.periodicity}, periodicity_detail = #{dto.periodicityDetail}, " +
            "folder_url = #{dto.folderUrl}, typology = #{dto.typology}, critical_table = #{dto.criticalTable}, " +
            "critical_table_owner = #{dto.criticalTableOwner}, l1t = #{dto.l1t}, hem = #{dto.hem}, his = #{dto.his}, " +
            "err = #{dto.err}, log = #{dto.log}, mlg = #{dto.mlg}, quality = #{dto.quality}, " +
            "tag1 = #{dto.tag1}, tag2 = #{dto.tag2}, tag3 = #{dto.tag3}, tag4 = #{dto.tag4}, raw_path = #{dto.rawPath}, " +
            "update_audit_user = #{dto.userId}, update_audit_user_name = #{dto.userName}, update_audit_date = NOW() " +
            "WHERE id = #{dto.id}")
    int updateSource(@Param("dto") SourceWithParameterDataDtoResponse dto);


}
