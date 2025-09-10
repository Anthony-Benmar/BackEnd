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

    @Select("CALL sidedb.SP_GET_STATUS_BY_ID(#{sourceId})")
    String getStatusById(@Param("sourceId") String sourceId);

    @Select("CALL sidedb.SP_COUNT_SOURCE_BY_ID(#{replacementId})")
    int countById(@Param("replacementId") String replacementId);

    @Select("{CALL sidedb.SP_GET_MAX_SOURCE_ID()}")
    String getMaxSourceId();

    @Select("CALL SP_INSERT_NEW_SOURCE(" +
            "#{dto.id}, #{dto.tdsDescription}, #{dto.tdsSource}, " +
            "#{dto.originType}, #{dto.sourceOrigin}, #{dto.status}, " +
            "#{dto.replacementId}, #{dto.modelOwner}, #{dto.tdsComments}, " +
            "#{dto.userId}, #{dto.userName})")
    void insertSource(@Param("dto") SourceWithParameterDataDtoResponse dto);
    @Update("CALL sidedb.SP_UPDATE_REPLACEMENT_ID(#{newReplacementIds}, #{oldId})")
    void updateReplacementId(@Param("newReplacementIds") String newReplacementIds,
                             @Param("oldId") String oldId);


    @Select("CALL sidedb.SP_GET_REPLACEMENT_IDS(#{id})")
    String getReplacementIds(@Param("id") String id);

    @Select("CALL SP_NEW_UPDATE_SOURCE(" +
            "#{dto.id}, #{dto.tdsDescription}, #{dto.tdsSource}, #{dto.sourceOrigin}, #{dto.originType}, #{dto.status}, " +
            "#{dto.replacementId}, #{dto.modelOwner}, #{dto.masterRegisteredBoard}, #{dto.dataLakeLayer}, " +
            "#{dto.uuaaRaw}, #{dto.uuaaMaster}, #{dto.tdsOpinionDebt}, #{dto.debtLevel}, #{dto.inheritedSourceId}, " +
            "#{dto.opinionDebtComments}, #{dto.missingCertification}, #{dto.missingFieldProfiling}, #{dto.incompleteOpinion}, " +
            "#{dto.pdcoProcessingUse}, #{dto.effectivenessDebt}, #{dto.ingestionType}, #{dto.ingestionLayer}, " +
            "#{dto.datioDownloadType}, #{dto.processingInputTableIds}, #{dto.periodicity}, #{dto.periodicityDetail}, " +
            "#{dto.folderUrl}, #{dto.typology}, #{dto.criticalTable}, #{dto.criticalTableOwner}, " +
            "#{dto.l1t}, #{dto.hem}, #{dto.his}, #{dto.err}, #{dto.log}, #{dto.mlg}, #{dto.quality}, " +
            "#{dto.tag1}, #{dto.tag2}, #{dto.tag3}, #{dto.tag4}, #{dto.rawPath}, " +
            "#{dto.userId}, #{dto.userName})")
    void updateSource(@Param("dto") SourceWithParameterDataDtoResponse dto);

    @Select("{CALL sidedb.SP_GET_COMMENTS_BY_SOURCE_AND_TYPE(#{sourceId, jdbcType=VARCHAR}, #{commentType, jdbcType=VARCHAR})}")
    List<String> getCommentsBySourceIdAndType(@Param("sourceId") String sourceId,
                                              @Param("commentType") String commentType);

    @Insert("{CALL sidedb.SP_SAVE_COMMENT_BY_SOURCE_AND_TYPE(" +
            "#{sourceId, jdbcType=VARCHAR}, " +
            "#{commentType, jdbcType=VARCHAR}, " +
            "#{comment, jdbcType=LONGVARCHAR})}")
    @Options(statementType = org.apache.ibatis.mapping.StatementType.CALLABLE)
    void saveCommentBySourceIdAndType(@Param("sourceId") String sourceId,
                                      @Param("commentType") String commentType,
                                      @Param("comment") String comment);

    @Select("CALL SP_INSERT_MODIFY_HISTORY(" +
            "#{dto.id}, #{dto.userId}, #{dto.userName}, #{dto.tdsDescription}, #{dto.tdsSource}, #{dto.sourceOrigin}, " +
            "#{dto.originType}, #{dto.status}, #{dto.replacementId}, #{dto.modelOwner}, #{dto.masterRegisteredBoard}, " +
            "#{dto.dataLakeLayer}, #{dto.uuaaRaw}, #{dto.uuaaMaster}, #{dto.tdsOpinionDebt}, #{dto.debtLevel}, " +
            "#{dto.inheritedSourceId}, #{dto.opinionDebtComments}, #{dto.missingCertification}, #{dto.missingFieldProfiling}, " +
            "#{dto.incompleteOpinion}, #{dto.pdcoProcessingUse}, #{dto.effectivenessDebt}, #{dto.ingestionType}, " +
            "#{dto.ingestionLayer}, #{dto.datioDownloadType}, #{dto.processingInputTableIds}, #{dto.periodicity}, " +
            "#{dto.periodicityDetail}, #{dto.folderUrl}, #{dto.typology}, #{dto.criticalTable}, #{dto.criticalTableOwner}, " +
            "#{dto.l1t}, #{dto.hem}, #{dto.his}, #{dto.err}, #{dto.log}, #{dto.mlg}, #{dto.quality}, " +
            "#{dto.tag1}, #{dto.tag2}, #{dto.tag3}, #{dto.tag4}, #{dto.rawPath})")
    void insertModifyHistory(@Param("dto") SourceWithParameterDataDtoResponse dto);
}
