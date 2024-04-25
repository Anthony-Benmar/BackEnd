package com.bbva.database.mappers;

import com.bbva.dto.project.request.InsertProjectDocumentDTO;
import com.bbva.dto.project.request.InsertProjectInfoDTORequest;
import com.bbva.dto.project.request.ProjectInfoDTO;
import com.bbva.dto.project.request.InsertProjectParticipantDTO;
import com.bbva.dto.project.request.SelectCalendarDTO;
import com.bbva.dto.project.response.ProjectInfoSelectAllByDomainDtoResponse;
import com.bbva.dto.project.response.ProjectInfoSelectByDomainDtoResponse;
import com.bbva.dto.project.response.ProjectInfoSelectResponse;
import com.bbva.entities.InsertEntity;
import com.bbva.entities.common.ProjectByPeriodEntity;
import com.bbva.entities.common.ProjectEntity;
import com.bbva.entities.project.ProjectPortafolioEntity;
import com.bbva.entities.project.ProjectFilterEntity;
import com.bbva.entities.project.ProjectPortafolioFilterEntity;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

public interface ProjectMapper {
    @Select("CALL SP_PROJECT_PAGED_FILTERED(" +
            "#{page}," +
            "#{records_amount}," +
            "#{sdatool_id}," +
            "#{project_name})")
    List<ProjectFilterEntity> filter(@Param("page") Integer page,
                                     @Param("records_amount") Integer records_amount,
                                     @Param("sdatool_id") String sdatool_id,
                                     @Param("project_name") String project_name);

    @Select("CALL SP_PROJECT_PORTFOLIO_PAGED_FILTERED(" +
            "#{pageCurrent}," +
            "#{recordsAmount}," +
            "#{projectId}," +
            "#{dominioId}," +
            "#{isRegulatory}," +
            "#{withSources})")
    @Results({
            @Result(property = "projectId", column = "project_id"),
            @Result(property = "projectName", column = "project_name"),
            @Result(property = "sdatoolId", column = "sdatool_id"),
            @Result(property = "portafolioCode", column = "portafolio_code"),
            @Result(property = "period", column = "periodo"),
            @Result(property = "sponsor", column = "sponsor"),
            @Result(property = "projectOwner", column = "project_owner"),
            @Result(property = "isProjectRegulatory", column = "regulatory_project_boolean"),
            @Result(property = "domain", column = "domain"),
            @Result(property = "recordsCount", column = "records_count"),
            @Result(property = "periodId", column = "period_id")
    })
    List<ProjectPortafolioFilterEntity> portafolioFilter(@Param("pageCurrent") int page,
                                                         @Param("recordsAmount") int recordsAmount,
                                                         @Param("projectId") int projectId,
                                                         @Param("dominioId") String dominioId,
                                                         @Param("isRegulatory") String isRegulatory,
                                                         @Param("withSources") Boolean withSources);

    @Select({"<script>" +
            "SELECT p.project_id,p.sdatool_id,p.project_name,p.status_type FROM data_project p " +
            "WHERE p.status_type = 1 AND p.project_id IN " +
            "<foreach item='item' index='index' collection='list' open='(' separator=',' close=')'> #{item} </foreach>" +
            "</script>"})
    List<ProjectEntity> readonly(@Param("list") int[] listId);

    @Select({"SELECT project_id, sdatool_id, project_name, status_type FROM data_project " +
            "WHERE project_id = #{projectId}"})
    ProjectEntity findById(@Param("projectId") int projectId);

    @Select({"<script>" +
            "SELECT p.project_id,p.sdatool_id,p.project_name,p.status_type FROM data_project p " +
            "WHERE p.status_type = 1 " +
            "</script>"})
    List<ProjectEntity> listforselect();

    @Select({"<script>" +
            "SELECT p.project_id,p.sdatool_id,p.project_name,p.status_type, a.period_id " +
            "FROM data_project_period a " +
            "LEFT JOIN data_project p ON p.project_id = a.project_id " +
            "WHERE p.status_type = 1 AND a.period_id = #{period_id}" +
            "</script>"})
    List<ProjectByPeriodEntity> listProjectsByPeriod(@Param("period_id") String period_id);

    @Insert("INSERT INTO data_project(project_name, project_desc, sdatool_id, status_type, product_owner_id, portafolio_code, " +
            "project_type, project_domain_type, sponsor_owner_id, rule_associated_link, " +
            "regulatory_project_boolean, operation_date, period_id) " +
            "VALUES (#{projectName},#{projectDesc},#{sdatoolId}, #{statusType}, #{productOwnerId}, #{portafolioCode}, " +
            "#{projectType}, #{projectDomainType}, #{sponsorOwnerId}, #{ruleAssociatedLink}, #{regulatoryProjectBoolean}, now(), #{periodId})")
    @Options(useGeneratedKeys = true, keyProperty = "projectId", keyColumn = "project_id")
    boolean insertProject(ProjectPortafolioEntity project);

    @Update("UPDATE data_project SET project_name = #{projectName}, project_desc = #{projectDesc}, sdatool_id= #{sdatoolId}," +
            "status_type = #{statusType}, product_owner_id= #{productOwnerId}, portafolio_code= #{portafolioCode}, " +
            "project_type=#{projectType}, project_domain_type=#{projectDomainType}, " +
            "sponsor_owner_id=#{sponsorOwnerId}, rule_associated_link=#{ruleAssociatedLink}, regulatory_project_boolean =#{regulatoryProjectBoolean}, period_id=#{periodId} " +
            "WHERE project_id = #{projectId}")
    boolean updateProject(ProjectPortafolioEntity project);

    @Update("UPDATE project_info SET sdatool_id= #{sdatoolId}, project_name = #{projectName}, project_desc = #{projectDesc}, " +
            "portafolio_code= #{portafolioCode}, regulatory_type =#{regulatoryType}, ttv_type=#{ttvType}, domain_id=#{domainId}, " +
            "project_type=#{projectType}, category_type=#{categoryType}, classification_type=#{classificationType}, " +
            "start_pi_id=#{startPiId}, end_pi_id=#{endPiId}, final_start_pi_id=#{finalStartPiId}, final_end_pi_id=#{finalEndPiId}, " +
            "wow_type=#{wowType}, country_priority_type=#{countryPriorityType}, status_type=#{statusType}, " +
            "update_audit_user=#{createAuditUser}, update_audit_date=CONVERT_TZ(NOW(), 'GMT', 'America/Lima') " +
            "WHERE project_id = #{projectId}")
    boolean updateProjectInfo(ProjectInfoDTO dto);

    @Delete("Delete from data_project WHERE project_id = #{projectId}")
    void deleteProject(@Param("projectId") int projectId);

    @Delete("DELETE FROM project_info WHERE project_id = #{projectId}")
    void deleteProjectInfo(@Param("projectId") int projectId);

    @Select("CALL SP_PROJECT_PORTFOLIO_DETAIL (#{projectId})")
    @Results({
            @Result(property = "projectId", column = "project_id"),
            @Result(property = "projectName", column = "project_name"),
            @Result(property = "projectDesc", column = "project_desc"),
            @Result(property = "sdatoolId", column = "sdatool_id"),
            @Result(property = "productOwnerId", column = "product_owner_id"),
            @Result(property = "portafolioCode", column = "portafolio_code"),
            @Result(property = "projectType", column = "project_type"),
            @Result(property = "projectDomainType", column = "project_domain_type"),
            @Result(property = "sponsorOwnerId", column = "sponsor_owner_id"),
            @Result(property = "sponsorOwnerName", column = "sponsor_owner_name"),
            @Result(property = "ruleAssociatedLink", column = "rule_associated_link"),
            @Result(property = "regulatoryProjectBoolean", column = "regulatory_project_boolean"),
            @Result(property = "periodId", column = "period_id"),
            @Result(property = "statusType", column = "status_type"),
    })
    ProjectPortafolioEntity getProjectById(@Param("projectId") int projectId);

    @Select("CALL SP_INSERT_PROJECT_DOCUMENT(" +
            "#{projectId}," +
            "#{documentType}," +
            "#{documentUrl}," +
            "#{createAuditUser})")
    @Results({
            @Result(property = "last_insert_id", column = "last_insert_id"),
            @Result(property = "new_register", column = "new_register")
    })
    InsertEntity insertProjectDocument(InsertProjectDocumentDTO dto);

    @Insert({
            "<script>",
            "INSERT INTO project_document",
            "(project_id, document_type, document_url, create_audit_date, create_audit_user)",
            "VALUES" +
                    "<foreach item='element' collection='listProjectDocuments' open='' separator=',' close=''>" +
                    "(" +
                    "#{element.projectId},",
                    "#{element.documentType},",
                    "#{element.documentUrl},",
                    "now(),",
                    "#{element.createAuditUser}" +
                    ")" +
                    "</foreach>",
            "</script>"})
    @Options(useGeneratedKeys = true, keyProperty = "documentId", keyColumn = "document_id")
    void insertProjectDocuments(@Param("listProjectDocuments")  List<InsertProjectDocumentDTO> projectDocumentsList);

    @Select("CALL SP_INSERT_PROJECT_PARTICIPANT(" +
            "#{participantUser}," +
            "#{participantName}," +
            "#{participantEmail}," +
            "#{projectId}," +
            "#{projectRolType}," +
            "#{piId}," +
            "#{createAuditUser})")
    @Results({
            @Result(property = "last_insert_id", column = "last_insert_id"),
            @Result(property = "new_register", column = "new_register")
    })
    InsertEntity insertProjectParticipant(InsertProjectParticipantDTO dto);

    @Insert({
            "<script>",
            "INSERT INTO project_participant",
            "(participant_user,participant_name, participant_email, project_id, project_rol_type, pi_id, create_audit_date, create_audit_user)",
            "VALUES" +
                    "<foreach item='element' collection='listProjectParticipants' open='' separator=',' close=''>" +
                    "(" +
                    "#{element.participantUser},",
                    "#{element.participantName},",
                    "#{element.participantEmail},",
                    "#{element.projectId},",
                    "#{element.projectRolType},",
                    "#{element.piId},",
                    "now(),",
                    "#{element.createAuditUser}" +
                    ")" +
                    "</foreach>",
            "</script>"})
    @Options(useGeneratedKeys = true, keyProperty = "projectParticipantId", keyColumn = "participant_id")
    void insertProjectParticipants(@Param("listProjectParticipants")  List<InsertProjectParticipantDTO> projectParticipantsList);

    @Select("CALL SP_INSERT_PROJECT_INFO(" +
            "#{sdatoolId}," +
            "#{projectName}," +
            "#{projectDesc}," +
            "#{portafolioCode}," +
            "#{regulatoryType}," +
            "#{ttvType}," +
            "#{domainId}," +
            "#{projectType}," +
            "#{categoryType}," +
            "#{classificationType}," +
            "#{startPiId}," +
            "#{endPiId}," +
            "#{finalStartPiId}," +
            "#{finalEndPiId}," +
            "#{wowType}," +
            "#{countryPriorityType}," +
            "#{createAuditUser}," +
            "#{statusType})")
    @Results({
            @Result(property = "last_insert_id", column = "last_insert_id"),
            @Result(property = "new_register", column = "new_register")
    })
    InsertEntity insertProjectInfo(InsertProjectInfoDTORequest dto);

    @Select("CALL SP_LIST_PROJECT(" +
            "#{projectId}," +
            "#{sdatoolIdOrProjectName}," +
            "#{domainId}," +
            "#{statusType}," +
            "#{projectType}," +
            "#{wowType})")
    @Results({
            @Result(property = "projectId", column = "project_id"),
            @Result(property = "sdatoolId", column = "sdatool_id"),
            @Result(property = "projectName", column = "project_name"),
            @Result(property = "projectDesc", column = "project_desc"),
            @Result(property = "portafolioCode", column = "portafolio_code"),
            @Result(property = "regulatoryType", column = "regulatory_type"),
            @Result(property = "ttvType", column = "ttv_type"),
            @Result(property = "domainId", column = "domain_id"),
            @Result(property = "domainName", column = "domain_name"),
            @Result(property = "projectType", column = "project_type"),
            @Result(property = "projectTypeDesc", column = "project_type_desc"),
            @Result(property = "categoryType", column = "category_type"),
            @Result(property = "classificationType", column = "classification_type"),
            @Result(property = "classificationTypeDesc", column = "classification_desc"),
            @Result(property = "startPiId", column = "start_pi_id"),
            @Result(property = "endPiId", column = "end_pi_id"),
            @Result(property = "finalStartPiId", column = "final_start_pi_id"),
            @Result(property = "finalEndPiId", column = "final_end_pi_id"),
            @Result(property = "statusType", column = "status_type"),
            @Result(property = "statusTypeDesc", column = "status_desc"),
            @Result(property = "wowType", column = "wow_type"),
            @Result(property = "countryPriorityType", column = "country_priority_type"),
            @Result(property = "createAuditDate", column = "create_audit_date"),
            @Result(property = "createAuditUser", column = "create_audit_user"),
            @Result(property = "updateAuditDate", column = "update_audit_date"),
            @Result(property = "updateAuditUser", column = "update_audit_user")
    })
    List<ProjectInfoSelectResponse> projectInfoFilter(@Param("projectId") int projectId,
                                                      @Param("sdatoolIdOrProjectName") String sdatoolId,
                                                      @Param("domainId") int domainId,
                                                      @Param("statusType") int statusType,
                                                      @Param("projectType") int projectType,
                                                      @Param("wowType") int wowType);
    @Select("CALL SP_LIST_ALL_PROJECT_BY_DOMAIN(" +
            "#{projectId}," +
            "#{domainId})")

    @Results({ @Result(property = "projectId", column = "project_id"),
            @Result(property = "sdatoolId", column = "sdatool_id"),
            @Result(property = "projectName", column = "project_name"),
            @Result(property = "projectDesc", column = "project_desc"),
            @Result(property = "portafolioCode", column = "portafolio_code"),
            @Result(property = "regulatoryType", column = "regulatory_type"),
            @Result(property = "ttvType", column = "ttv_type"),
            @Result(property = "domainId", column = "domain_id"),
            @Result(property = "projectType", column = "project_type"),
            @Result(property = "categoryType", column = "category_type"),
            @Result(property = "classificationType", column = "classification_type"),
            @Result(property = "startPiId", column = "start_pi_id"),
            @Result(property = "endPiId", column = "end_pi_id"),
            @Result(property = "finalStartPiId", column = "final_start_pi_id"),
            @Result(property = "finalEndPiId", column = "final_end_pi_id"),
            @Result(property = "statusType", column = "status_type"),
            @Result(property = "wowType", column = "wow_type"),
            @Result(property = "countryPriorityType", column = "country_priority_type"),
            @Result(property = "createAuditDate", column = "create_audit_date"),
            @Result(property = "createAuditUser", column = "create_audit_user"),
            @Result(property = "updateAuditDate", column = "update_audit_date"),
            @Result(property = "updateAuditUser", column = "update_audit_user") })

    List<ProjectInfoSelectAllByDomainDtoResponse> projectInfoFilterAllByDomain(@Param("projectId") int projectId,
                                                                         @Param("domainId") int domainId);
@Select("CALL SP_LIST_PROJECT_BY_DOMAIN(" +
            "#{projectId}," +
            "#{domainId})")

    @Results({
            @Result(property = "projectId", column = "project_id"),
            @Result(property = "projectName", column = "project_name"),
            @Result(property = "domainId", column = "domain_id")
    })
    List<ProjectInfoSelectByDomainDtoResponse> projectInfoFilterByDomain(@Param("projectId") int projectId,
                                                                               @Param("domainId") int domainId);

    @Delete("CALL SP_DELETE_DOCUMENT(#{projectId}, #{documentId}, #{updateAuditUser})")
    void deleteDocument(@Param("projectId") int projectId, @Param("documentId") int documentId, @Param("updateAuditUser") String updateAuditUser);

    @Update("CALL SP_UPDATE_DOCUMENT(#{documentId}, #{projectId}, #{documentType}, #{documentUrl}, #{createAuditUser})")
    boolean updateDocument(InsertProjectDocumentDTO dto);

    @Select("CALL SP_PROJECT_DOCUMENT_FILTERED(#{projectId}, #{documentId})")
    @Results({
            @Result(property = "projectId", column = "project_id"),
            @Result(property = "documentId", column = "document_id"),
            @Result(property = "documentType", column = "document_type"),
            @Result(property = "documentUrl", column = "document_url"),
            @Result(property = "createAuditUser", column = "last_user_modified")
    })
    List<InsertProjectDocumentDTO> getDocument(@Param("projectId") int projectId,
                                               @Param("documentId") int documentId);

    @Delete("CALL SP_DELETE_PROJECT_PARTICIPANT(#{projectId}, #{participantId}, #{updateAuditUser})")
    void deleteParticipantProject(@Param("projectId") int projectId, @Param("participantId") int participantId, @Param("updateAuditUser") String updateAuditUser);

    @Update("CALL SP_UPDATE_PROJECT_PARTICIPANT(" +
            "#{participantUser}," +
            "#{participantName}," +
            "#{participantEmail}," +
            "#{projectId}," +
            "#{projectRolType}," +
            "#{piId}," +
            "#{createAuditUser}," +
            "#{projectParticipantId})")
    boolean updateParticipant(InsertProjectParticipantDTO dto);

    @Select("CALL SP_PROJECT_PARTICIPANTS(#{projectId})")
    @Results({
            @Result(property = "projectParticipantId", column = "participant_id"),
            @Result(property = "participantName", column = "participant_name"),
            @Result(property = "participantUser", column = "participant_user"),
            @Result(property = "participantEmail", column = "participant_email"),
            @Result(property = "projectId", column = "project_id"),
            @Result(property = "projectRolType", column = "project_rol_type"),
            @Result(property = "piId", column = "pi_id"),
            @Result(property = "createAuditDate", column = "create_audit_date"),
            @Result(property = "createAuditUser", column = "create_audit_user"),
            @Result(property = "updateAuditDate", column = "update_audit_date"),
            @Result(property = "updateAuditUser", column = "update_audit_user")
    })
    List<InsertProjectParticipantDTO> getProjectParticipants(@Param("projectId") int projectId);

    @Select("select * from calendar_pi order by pi_large_name desc")
    @Results({
            @Result(property = "piId", column = "pi_id"),
            @Result(property = "piShortName", column = "pi_short_name"),
            @Result(property = "piLargeName", column = "pi_large_name"),
            @Result(property = "piYearId", column = "pi_year_id"),
            @Result(property = "piQuarterId", column = "pi_quarter_id"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "createAuditDate", column = "create_audit_date"),
            @Result(property = "createAuditUser", column = "create_audit_user"),
            @Result(property = "updateAuditDate", column = "update_audit_date"),
            @Result(property = "updateAuditUser", column = "update_audit_user")
    })
    List<SelectCalendarDTO> getAllCalendar();

    @Select("SELECT COUNT(*) FROM project_info WHERE sdatool_id = #{sdatoolId}")
    int countBySdatoolId(String sdatoolId);

    @Select("SELECT COUNT(*) FROM project_info WHERE sdatool_id = #{sdatoolId} AND project_id != #{projectId}")
    int countBySdatoolIdUpdate(String sdatoolId, int projectId);


    @Select("SELECT project_id, sdatool_id, project_name FROM project_info WHERE domain_id = #{domain_id}")
    List<ProjectInfoSelectResponse> listProjectsByDomain(@Param("domain_id") int domain_id);


}
