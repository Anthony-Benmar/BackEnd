package com.bbva.database.mappers;

import com.bbva.dto.source_with_parameter.response.SourceWithParameterDataDtoResponse;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SourceWithParameterMapper {
    @Select("CALL GET_SOURCES_WITH_PARAMETER(#{limit}, #{offset}, #{tdsDescription}, #{tdsSource}, #{replacementId}, #{originType}, #{tdsOpinionDebt}, #{effectivenessDebt})")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "tdsDescription", column = "tds_description"),
            @Result(property = "tdsSource", column = "tds_source"),
            @Result(property = "sourceOrigin", column = "source_origin"),
            @Result(property = "originType", column = "origin_type"),
            @Result(property = "status", column = "status"),
            @Result(property = "replacementId", column = "replacement_id"),
            @Result(property = "modelOwner", column = "model_owner"),
            @Result(property = "masterRegisteredBoard", column = "master_registered_board"),
            @Result(property = "dataLakeLayer", column = "datalake_layer"),
            @Result(property = "uuaaRaw", column = "uuaa_raw"),
            @Result(property = "uuaaMaster", column = "uuaa_master"),
            @Result(property = "tdsOpinionDebt", column = "tds_opinion_debt"),
            @Result(property = "debtLevel", column = "debt_level"),
            @Result(property = "inheritedSourceId", column = "inherited_source_id"),
            @Result(property = "opinionDebtComments", column = "opinion_debt_comments"),
            @Result(property = "missingCertification", column = "missing_certification"),
            @Result(property = "missingFieldProfiling", column = "missing_field_profiling"),
            @Result(property = "incompleteOpinion", column = "incomplete_opinion"),
            @Result(property = "pdcoProcessingUse", column = "pdco_processing_use"),
            @Result(property = "effectivenessDebt", column = "effectiveness_debt"),
            @Result(property = "ingestionType", column = "ingestion_type"),
            @Result(property = "ingestionLayer", column = "ingestion_layer"),
            @Result(property = "datioDownloadType", column = "datio_download_type"),
            @Result(property = "processingInputTableIds", column = "processing_input_table_ids"),
            @Result(property = "periodicity", column = "periodicity"),
            @Result(property = "periodicityDetail", column = "periodicity_detail"),
            @Result(property = "folderUrl", column = "folder_url"),
            @Result(property = "typology", column = "typology"),
            @Result(property = "criticalTable", column = "critical_table"),
            @Result(property = "criticalTableOwner", column = "critical_table_owner"),
            @Result(property = "l1t", column = "l1t"),
            @Result(property = "hem", column = "hem"),
            @Result(property = "his", column = "his"),
            @Result(property = "err", column = "err"),
            @Result(property = "log", column = "log"),
            @Result(property = "mlg", column = "mlg"),
            @Result(property = "quality", column = "quality"),
            @Result(property = "tag1", column = "tag1"),
            @Result(property = "tag2", column = "tag2"),
            @Result(property = "tag3", column = "tag3"),
            @Result(property = "tag4", column = "tag4"),
            @Result(property = "rawPath", column = "raw_path")
    })
    List<SourceWithParameterDataDtoResponse> getSourcesWithParameterWithFilters(
            @Param("limit") int limit,
            @Param("offset") int offset,
            @Param("tdsDescription") String projectName,
            @Param("tdsSource") String tdsSource,
            @Param("replacementId") String replacementId,
            @Param("originType") String originType,
            @Param("tdsOpinionDebt") String tdsOpinionDebt,
            @Param("effectivenessDebt") String effectivenessDebt
    );
    @Select("CALL GET_SOURCES_WITH_PARAMETER_TOTAL(#{tdsDescription}, #{tdsSource}, #{replacementId}," +
            " #{originType}, #{tdsOpinionDebt}, #{effectivenessDebt})")
    int getSourcesWithParameterTotalCountWithFilters(
            @Param("tdsDescription") String tdsDescription,
            @Param("tdsSource") String tdsSource,
            @Param("replacementId") String replacementId,
            @Param("originType") String originType,
            @Param("tdsOpinionDebt") String tdsOpinionDebt,
            @Param("effectivenessDebt") String effectivenessDebt
    );
    @Select("SELECT DISTINCT tds_description FROM sources WHERE tds_description IS NOT NULL")
    List<String> getTdsDescription();

    @Select("SELECT DISTINCT tds_source FROM sources WHERE tds_source IS NOT NULL")
    List<String> getTdsSource();

    @Select("SELECT DISTINCT replacement_id FROM sources WHERE replacement_id IS NOT NULL")
    List<String> getReplacementId();

    @Select("SELECT DISTINCT origin_type FROM sources WHERE origin_type IS NOT NULL")
    List<String> getOriginType();

    @Select("SELECT DISTINCT tds_opinion_debt FROM sources WHERE tds_opinion_debt IS NOT NULL")
    List<String> getTdsOpinionDebt();

    @Select("SELECT DISTINCT effectiveness_debt FROM sources WHERE effectiveness_debt IS NOT NULL")
    List<String> getEffectivenessDebt();

    @Select("select * from sources where id = #{sourceWithParameterId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "tdsDescription", column = "tds_description"),
            @Result(property = "tdsSource", column = "tds_source"),
            @Result(property = "sourceOrigin", column = "source_origin"),
            @Result(property = "originType", column = "origin_type"),
            @Result(property = "status", column = "status"),
            @Result(property = "replacementId", column = "replacement_id"),
            @Result(property = "modelOwner", column = "model_owner"),
            @Result(property = "masterRegisteredBoard", column = "master_registered_board"),
            @Result(property = "dataLakeLayer", column = "datalake_layer"),
            @Result(property = "uuaaRaw", column = "uuaa_raw"),
            @Result(property = "uuaaMaster", column = "uuaa_master"),
            @Result(property = "tdsOpinionDebt", column = "tds_opinion_debt"),
            @Result(property = "debtLevel", column = "debt_level"),
            @Result(property = "inheritedSourceId", column = "inherited_source_id"),
            @Result(property = "opinionDebtComments", column = "opinion_debt_comments"),
            @Result(property = "missingCertification", column = "missing_certification"),
            @Result(property = "missingFieldProfiling", column = "missing_field_profiling"),
            @Result(property = "incompleteOpinion", column = "incomplete_opinion"),
            @Result(property = "pdcoProcessingUse", column = "pdco_processing_use"),
            @Result(property = "effectivenessDebt", column = "effectiveness_debt"),
            @Result(property = "ingestionType", column = "ingestion_type"),
            @Result(property = "ingestionLayer", column = "ingestion_layer"),
            @Result(property = "datioDownloadType", column = "datio_download_type"),
            @Result(property = "processingInputTableIds", column = "processing_input_table_ids"),
            @Result(property = "periodicity", column = "periodicity"),
            @Result(property = "periodicityDetail", column = "periodicity_detail"),
            @Result(property = "folderUrl", column = "folder_url"),
            @Result(property = "typology", column = "typology"),
            @Result(property = "criticalTable", column = "critical_table"),
            @Result(property = "criticalTableOwner", column = "critical_table_owner"),
            @Result(property = "l1t", column = "l1t"),
            @Result(property = "hem", column = "hem"),
            @Result(property = "his", column = "his"),
            @Result(property = "err", column = "err"),
            @Result(property = "log", column = "log"),
            @Result(property = "mlg", column = "mlg"),
            @Result(property = "quality", column = "quality"),
            @Result(property = "tag1", column = "tag1"),
            @Result(property = "tag2", column = "tag2"),
            @Result(property = "tag3", column = "tag3"),
            @Result(property = "tag4", column = "tag4"),
            @Result(property = "rawPath", column = "raw_path")
    })
    SourceWithParameterDataDtoResponse getSourceWithParameterById(@Param("sourceWithParameterId") Integer sourceWithParameterId);
}
