package com.bbva.database.mappers;
import com.bbva.dto.government.request.InsertConceptRequestDTO;
import com.bbva.dto.government.request.InsertSourceRequestDTO;
import com.bbva.dto.government.request.UpdateConceptRequestDTO;
import com.bbva.dto.government.request.UpdateSourceRequestDTO;
import com.bbva.dto.government.response.SourceDefinitionDTOResponse;
import com.bbva.entities.InsertEntity;
import com.bbva.entities.UpdateEntity;
import com.bbva.entities.government.SourceConceptEntity;
import com.bbva.entities.government.SourceDefinitionEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;
public interface GovernmentMapper {

    @Select("CALL SP_FILTER_SOURCE(" +
            "#{pageCurrent}," +
            "#{recordsAmount}," +
            "#{projectId}," +
            "#{dominioId})")
    @Results({
            @Result(property = "ucSourceId", column = "uc_source_id"),
            @Result(property = "useCaseId", column = "use_case_id"),
            @Result(property = "ucSourceName", column = "uc_source_name"),
            @Result(property = "ucSourceDesc", column = "uc_source_desc"),
            @Result(property = "ucSourceType", column = "uc_source_type"),
            @Result(property = "elementName", column = "element_name"),
            @Result(property = "ucFrequencyType", column = "uc_frequency_type"),
            @Result(property = "depthMonthNumber", column = "depth_month_number"),
            @Result(property = "ansDesc", column = "ans_desc"),
            @Result(property = "priorityNumber", column = "priority_number"),
            @Result(property = "recordsCount", column = "records_count")
    })
    List<SourceDefinitionDTOResponse> sourceFilter(@Param("pageCurrent") int page,
                                                   @Param("recordsAmount") int recordsAmount,
                                                   @Param("projectId") int projectId,
                                                   @Param("dominioId") String dominioId);

    @Select("SELECT s.uc_source_id,p.project_id,p.project_name,p.portafolio_code, s.use_case_id, s.uc_source_name, s.uc_source_desc, s.uc_source_type,c.element_name, s.uc_frequency_type,s.depth_month_number,s.ans_desc,s.priority_number,s.system_owner_id " +
            "FROM data_use_case_source s " +
            "LEFT JOIN data_use_case_definition d ON d.use_case_id=s.use_case_id " +
            "LEFT JOIN data_project p ON p.project_id=d.project_id " +
            "LEFT JOIN catalog c ON c.element_id = s.uc_source_type AND c.catalog_id =1003 " +
            "WHERE s.uc_source_id = #{uc_source_id}")
    SourceDefinitionEntity getSourceById(@Param("uc_source_id") Integer uc_source_id);

    @Select("CALL SP_INSERT_SOURCE(" +
            "#{project_id}," +
            "#{uc_source_name}," +
            "#{uc_source_desc}," +
            "#{source_id}," +
            "#{depth_month_number}," +
            "#{uc_frequency_type}," +
            "#{status_type}," +
            "#{operation_user}," +
            "#{uc_source_type}," +
            "#{ans_desc}," +
            "#{system_owner_id}," +
            "#{priority_number})")
    @Results({
            @Result(property = "last_insert_id", column = "last_insert_id"),
            @Result(property = "new_register", column = "new_register")
    })
    InsertEntity insertSourceDef(InsertSourceRequestDTO entity);

    @Select("CALL SP_INSERT_CONCEPT(" +
            "#{uc_data_code}," +
            "#{uc_source_id}," +
            "#{uc_data_group_desc}," +
            "#{uc_data_func_name}," +
            "#{uc_data_desc}," +
            "#{uc_data_example}," +
            "#{physical_name}," +
            "#{status_type}," +
            "#{user_comment_desc}," +
            "#{operation_user}," +
            "#{relevant_field_bool}," +
            "#{cci_field_bool}," +
            "#{field_type}," +
            "#{granularity_data_type}," +
            "#{field_domain_desc}," +
            "#{field_subdomain_desc}," +
            "#{data_owner_id}," +
            "#{ownership_id})")
    @Results({
            @Result(property = "last_insert_id", column = "last_insert_id"),
            @Result(property = "new_register", column = "new_register")
    })
    InsertEntity insertConcept(InsertConceptRequestDTO entity);

    @Select("CALL SP_UPDATE_SOURCE(" +
            "#{uc_source_id}," +
            "#{project_id}," +
            "#{uc_source_name}," +
            "#{uc_source_desc}," +
            "#{source_id}," +
            "#{depth_month_number}," +
            "#{uc_frequency_type}," +
            "#{status_type}," +
            "#{operation_user}," +
            "#{uc_source_type}," +
            "#{ans_desc}," +
            "#{system_owner_id}," +
            "#{priority_number})")
    @Results({
            @Result(property = "last_updated_id", column = "last_updated_id"),
            @Result(property = "updated_register", column = "updated_register")
    })
    UpdateEntity updateSourceDef(UpdateSourceRequestDTO entity);

    @Select("CALL SP_UPDATE_CONCEPT(" +
            "#{uc_data_id}," +
            "#{uc_data_code}," +
            "#{uc_source_id}," +
            "#{uc_data_group_desc}," +
            "#{uc_data_func_name}," +
            "#{uc_data_desc}," +
            "#{uc_data_example}," +
            "#{physical_name}," +
            "#{status_type}," +
            "#{user_comment_desc}," +
            "#{operation_user}," +
            "#{relevant_field_bool}," +
            "#{cci_field_bool}," +
            "#{field_type}," +
            "#{granularity_data_type}," +
            "#{field_domain_desc}," +
            "#{field_subdomain_desc}," +
            "#{data_owner_id}," +
            "#{ownership_id})")
    @Results({
            @Result(property = "last_updated_id", column = "last_updated_id"),
            @Result(property = "updated_register", column = "updated_register")
    })
    UpdateEntity updateConcept(UpdateConceptRequestDTO entity);

    @Delete("DELETE FROM data_use_case_data " +
            "WHERE uc_data_id=#{uc_data_id};")
    boolean deleteConcept(@Param("uc_data_id") Integer uc_data_id);

    @Delete("DELETE FROM data_use_case_source " +
            "WHERE uc_source_id=#{uc_source_id};")
    boolean deleteSource(@Param("uc_source_id") Integer uc_source_id);

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
