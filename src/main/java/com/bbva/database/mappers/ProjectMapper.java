package com.bbva.database.mappers;

import com.bbva.dto.project.request.InsertProjectDocumentDTO;
import com.bbva.dto.project.request.InsertProjectParticipantDTO;
import com.bbva.entities.InsertEntity;
import com.bbva.entities.common.ProjectByPeriodEntity;
import com.bbva.entities.common.ProjectEntity;
import com.bbva.entities.project.ProjectPortafolioEntity;
import com.bbva.entities.project.ProjectFilterEntity;
import com.bbva.entities.project.ProjectPortafolioFilterEntity;
import org.apache.ibatis.annotations.*;

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

    @Update("UPDATE data_project SET status_type = 0 " +
            "WHERE project_id = #{projectId}")
    boolean deleteProject(@Param("projectId") int projectId);

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

    @Select("CALL SP_INSERT_PROJECT_PARTICIPANT(" +
            "#{participantUser}," +
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
}
