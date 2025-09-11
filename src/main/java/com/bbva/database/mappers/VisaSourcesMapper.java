package com.bbva.database.mappers;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

import com.bbva.dto.source_with_parameter.response.SourceWithParameterDataDtoResponse;
import com.bbva.dto.visa_sources.request.ApproveVisaSourceDtoRequest;
import com.bbva.dto.visa_sources.request.RegisterVisaSourceDtoRequest;
import com.bbva.dto.visa_sources.request.UpdateStatusVisaSourceDtoRequest;
import com.bbva.dto.visa_sources.request.VisaSourcePaginationDtoRequest;
import com.bbva.dto.visa_sources.response.VisaSourceApproveDtoResponse;
import com.bbva.dto.visa_sources.response.VisaSourcesDataDtoResponse;
import com.bbva.entities.InsertEntity;
import com.bbva.entities.UpdateEntity;

import java.util.List;

public interface VisaSourcesMapper {
    @Select("CALL GET_VISA_SOURCES(#{filter.limit}, #{filter.offset}, #{filter.id}," +
            "#{filter.quarter}, #{filter.registerDate}, #{filter.domain}, #{filter.userStory})")
            @Result(property = "id", column = "id")
            @Result(property = "sourceType", column = "source_type")
            @Result(property = "userStory", column = "user_story")
            @Result(property = "quarter", column = "quarter")
            @Result(property = "registerDate", column = "register_date")
            @Result(property = "sdatoolProject", column = "sdatool_project")
            @Result(property = "sdatoolFinal", column = "sdatool_final")
            @Result(property = "functionalAnalist", column = "functional_analist")
            @Result(property = "domain", column = "domain")
            @Result(property = "folio", column = "folio")
            @Result(property = "tdsProposalName", column = "tds_proposal_name")
            @Result(property = "tdsDescription", column = "tds_description")
            @Result(property = "tdsProof", column = "tds_proof")
            @Result(property = "tdsProof2", column = "tds_proof2")
            @Result(property = "originSource", column = "origin_source")
            @Result(property = "originType", column = "origin_type")
            @Result(property = "ownerModel", column = "owner_model")
            @Result(property = "uuaaRaw", column = "uuaa_raw")
            @Result(property = "uuaaMaster", column = "uuaa_master")
            @Result(property = "criticalTable", column = "critical_table")
            @Result(property = "functionalChecklist", column = "functional_checklist")
            @Result(property = "structure", column = "structure")
            @Result(property = "status", column = "status")
    List<VisaSourcesDataDtoResponse> getVisaSourceWithFilters(@Param("filter") VisaSourcePaginationDtoRequest filter);

    @Select("CALL GET_VISA_SOURCES_TOTAL(#{filter.id}," +
            "#{filter.quarter}, #{filter.registerDate}, #{filter.domain}, #{filter.userStory})")
    int getVisaSourcesTotalCountWithFilters(
        @Param("filter") VisaSourcePaginationDtoRequest filter
    );

    @Select("CALL SP_INSERT_VISA_SOURCE(" +
            "#{sourceType}," +
            "#{userStory}," +
            "#{quarter}," +
            "#{registerDate}," +
            "#{sdatoolProject}," +
            "#{sdatoolFinal}," +
            "#{functionalAnalist}," +
            "#{domain}," +
            "#{folio}," +
            "#{tdsProposalName}," +
            "#{tdsDescription}," +
            "#{tdsProof}," +
            "#{tdsProof2}," +
            "#{originSource}," +
            "#{originType}," +
            "#{ownerModel}," +
            "#{uuaaRaw}," +
            "#{uuaaMaster}," +
            "#{criticalTable}," +
            "#{functionalChecklist}," +
            "#{structure}," +
            "#{status})")
        @Result(property = "last_insert_id", column = "last_insert_id")
        @Result(property = "new_register", column = "new_register")
    InsertEntity insertVisaSourceEntity(RegisterVisaSourceDtoRequest entity);

    @Select("CALL SP_UPDATE_VISA_SOURCE(" +
            "#{id}," +
            "#{sourceType}," +
            "#{userStory}," +
            "#{quarter}," +
            "#{registerDate}," +
            "#{sdatoolProject}," +
            "#{sdatoolFinal}," +
            "#{functionalAnalist}," +
            "#{domain}," +
            "#{folio}," +
            "#{tdsProposalName}," +
            "#{tdsDescription}," +
            "#{tdsProof}," +
            "#{tdsProof2}," +
            "#{originSource}," +
            "#{originType}," +
            "#{ownerModel}," +
            "#{uuaaRaw}," +
            "#{uuaaMaster}," +
            "#{criticalTable}," +
            "#{functionalChecklist}," +
            "#{structure}," +
            "#{status})")
        @Result(property = "last_updated_id", column = "last_updated_id")
        @Result(property = "updated_register", column = "updated_register")
    UpdateEntity updateVisaSourceEntity(RegisterVisaSourceDtoRequest entity);

    @Select("SELECT * FROM visa_sources WHERE id = #{id}")
        @Result(property = "id", column = "id")
        @Result(property = "sourceType", column = "source_type")
        @Result(property = "userStory", column = "user_story")
        @Result(property = "quarter", column = "quarter")
        @Result(property = "registerDate", column = "register_date")
        @Result(property = "sdatoolProject", column = "sdatool_project")
        @Result(property = "sdatoolFinal", column = "sdatool_final")
        @Result(property = "functionalAnalist", column = "functional_analist")
        @Result(property = "domain", column = "domain")
        @Result(property = "folio", column = "folio")
        @Result(property = "tdsProposalName", column = "tds_proposal_name")
        @Result(property = "tdsDescription", column = "tds_description")
        @Result(property = "tdsProof", column = "tds_proof")
        @Result(property = "originSource", column = "origin_source")
        @Result(property = "originType", column = "origin_type")
        @Result(property = "ownerModel", column = "owner_model")
        @Result(property = "uuaaRaw", column = "uuaa_raw")
        @Result(property = "uuaaMaster", column = "uuaa_master")
        @Result(property = "criticalTable", column = "critical_table")
        @Result(property = "functionalChecklist", column = "functional_checklist")
        @Result(property = "structure", column = "structure")
        @Result(property = "status", column = "status")
    VisaSourcesDataDtoResponse getVisaSourceById(Integer id);

    @Select("CALL SP_APPROVE_VISA_SOURCE(" +
        "#{dto.id}," +
        "#{dto.isSubstitution}," +
        "#{dto.isMinorChange}," +
        "#{dto.sourceId})")
        @Result(property = "message", column = "message")
        @Result(property = "id", column = "id")
    VisaSourceApproveDtoResponse approveVisaSource(@Param("dto") ApproveVisaSourceDtoRequest dto);

    @Select("UPDATE visa_sources SET status = #{dto.status} WHERE id = #{dto.id}")
    void updateStatusVisaSource(@Param("dto") UpdateStatusVisaSourceDtoRequest dto);

    @Select("SELECT id, replacement_id as replacementId FROM sources WHERE FIND_IN_SET(id, #{ids});")
    List<SourceWithParameterDataDtoResponse> validateSourceIds(String ids);
}
