package com.bbva.database.mappers;
import com.bbva.entities.government.SourceConceptEntity;
import com.bbva.entities.government.SourceDefinitionEntity;
import com.bbva.entities.map_dependecy.MapDependencyEntity;
import com.bbva.entities.project.ProjectFilterEntity;
import com.bbva.entities.project.ProjectPortafolioEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;
public interface GovernmentMapper {

    @Select("SELECT s.uc_source_id, s.use_case_id, s.uc_source_name, s.uc_source_desc, s.uc_source_type,c.element_name, s.uc_frequency_type,s.depth_month_number,s.ans_desc,s.priority_number "+
            "FROM data_use_case_source s "+
            "LEFT JOIN data_use_case_definition d ON d.use_case_id=s.use_case_id "+
            "LEFT JOIN catalog c ON c.element_id = s.uc_source_type AND c.catalog_id =1003 "+
            "WHERE d.project_id = #{project_id}")
    List<SourceDefinitionEntity> listSources(@Param("project_id") Integer project_id);

    @Select("SELECT s.uc_source_id,p.project_id,p.project_name,p.portafolio_code, s.use_case_id, s.uc_source_name, s.uc_source_desc, s.uc_source_type,c.element_name, s.uc_frequency_type,s.depth_month_number,s.ans_desc,s.priority_number,s.system_owner_id " +
            "FROM data_use_case_source s " +
            "LEFT JOIN data_use_case_definition d ON d.use_case_id=s.use_case_id " +
            "LEFT JOIN data_project p ON p.project_id=d.project_id " +
            "LEFT JOIN catalog c ON c.element_id = s.uc_source_type AND c.catalog_id =1003 " +
            "WHERE s.uc_source_id = #{uc_source_id}")
    SourceDefinitionEntity getSourceById(@Param("uc_source_id") Integer uc_source_id);

    @Insert("INSERT INTO data_use_case_source (use_case_id,uc_source_name,uc_source_desc,source_id,depth_month_number,uc_frequency_type,status_type,operation_user,operation_date,uc_source_type,ans_desc,system_owner_id,priority_number)" +
            "VALUES (#{use_case_id},#{uc_source_name},#{uc_source_desc},#{source_id},#{depth_month_number},#{uc_frequency_type},#{status_type},#{operation_user},now(),#{uc_source_type},#{ans_desc},#{system_owner_id},#{priority_number})")
    @Options(useGeneratedKeys = true, keyProperty = "uc_source_id", keyColumn = "uc_source_id")
    boolean insertSourceDef(SourceDefinitionEntity entity);

    @Insert("INSERT INTO data_use_case_data (uc_data_code,uc_source_id,uc_data_group_desc,uc_data_func_name,uc_data_desc,uc_data_example,physical_name,status_type,user_comment_desc,operation_user,operation_date,relevant_field_bool,cci_field_bool,field_type,granularity_data_type,field_domain_desc,field_subdomain_desc,data_owner_id,ownership_id) "+
            "VALUES (#{uc_data_code},#{uc_source_id},#{uc_data_group_desc},#{uc_data_func_name},#{uc_data_desc},#{uc_data_example},#{physical_name},#{status_type},#{user_comment_desc},#{operation_user},now(),#{relevant_field_bool},#{cci_field_bool},#{field_type},#{granularity_data_type},#{field_domain_desc},#{field_subdomain_desc},#{data_owner_id},#{ownership_id})")
    @Options(useGeneratedKeys = true, keyProperty = "uc_data_id", keyColumn = "uc_data_id")
    boolean insertConcept(SourceConceptEntity entity);

    @Update("UPDATE data_use_case_source " +
            "SET uc_source_name = #{uc_source_name},uc_source_desc = #{uc_source_desc},depth_month_number = #{depth_month_number},uc_frequency_type = #{uc_frequency_type},operation_user = #{operation_user},uc_source_type = #{uc_source_type},ans_desc = #{ans_desc},system_owner_id = #{system_owner_id},priority_number = #{priority_number},operation_date = NOW() " +
            "WHERE uc_source_id= #{uc_source_id};")
    boolean updateSourceDef(SourceDefinitionEntity entity);

    @Update("UPDATE data_use_case_data "+
            "SET uc_data_code = #{uc_data_code},uc_source_id = #{uc_source_id},uc_data_group_desc = #{uc_data_group_desc},uc_data_func_name = #{uc_data_func_name},uc_data_desc = #{uc_data_desc},uc_data_example = #{uc_data_example},physical_name = #{physical_name},status_type = #{status_type},user_comment_desc  = #{user_comment_desc },operation_user = #{operation_user},operation_date =NOW(),relevant_field_bool = #{relevant_field_bool},cci_field_bool = #{cci_field_bool},field_type = #{field_type},granularity_data_type = #{granularity_data_type},field_domain_desc = #{field_domain_desc},field_subdomain_desc = #{field_subdomain_desc},data_owner_id = #{data_owner_id},ownership_id = #{ownership_id} "+
            "WHERE uc_data_id= #{uc_data_id};")
    boolean updateConcept(SourceConceptEntity entity);

    @Delete("DELETE FROM data_use_case_data " +
            "WHERE uc_data_id=#{uc_data_id};")
    boolean deleteConcept(@Param("uc_data_id") Integer uc_data_id);

    @Delete("DELETE FROM data_map_dependency " +
            "WHERE map_dependency_id = #{dependencyId};")
    boolean deleteMapDependency(@Param("dependencyId") Integer dependencyId);

    @Select("SELECT d.uc_data_id, d.uc_data_code, d.uc_source_id, d.uc_data_group_desc, d.uc_data_func_name, d.uc_data_desc, d.uc_data_example, d.physical_name, d.status_type, d.user_comment_desc, " +
            "d.operation_user,d.operation_date,d.relevant_field_bool,d.cci_field_bool,d.field_type,c.element_name,d.granularity_data_type,d.field_domain_desc,d.field_subdomain_desc,d.data_owner_id,d.ownership_id " +
            "FROM data_use_case_data d " +
            "LEFT JOIN catalog c ON c.element_id = d.field_type AND c.catalog_id =1029 " +
            "where uc_source_id = #{uc_source_id}")
    List<SourceConceptEntity> listSourceConcepts(@Param("uc_source_id") Integer uc_source_id);
}
