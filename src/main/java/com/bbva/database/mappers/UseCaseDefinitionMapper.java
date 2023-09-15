package com.bbva.database.mappers;

import com.bbva.dto.map_dependency.response.MapDependencyListByProjectResponse;
import com.bbva.entities.use_case_definition.UseCaseDefinitionEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UseCaseDefinitionMapper {

    @Insert("INSERT INTO  data_use_case_definition(project_id, status_type, sts_link, msa_link, operation_date) " +
            "VALUES (#{projectId},#{statusType},#{stsLink}, #{msaLink}, now())")
    @Options(useGeneratedKeys = true, keyProperty = "useCaseId", keyColumn = "use_case_id")
    boolean insert(UseCaseDefinitionEntity useCaseDefinition);

    @Select({"SELECT use_case_id, project_id, status_type, sts_link, msa_link " +
            "FROM data_use_case_definition " +
            "WHERE project_id = #{projectId}"})
    @Results({
            @Result(property = "useCaseId", column = "use_case_id"),
            @Result(property = "projectId", column = "project_id"),
            @Result(property = "statusType", column = "status_type"),
            @Result(property = "stsLink", column = "sts_link"),
            @Result(property = "msaLink", column = "msa_link")
    })
    List<UseCaseDefinitionEntity> listUseCaseDefinitionByProjectId(@Param("projectId") int projectId);

}
