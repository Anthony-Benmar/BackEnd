package com.bbva.database.mappers;

import com.bbva.dto.map_dependency.response.MapDependencyListByProjectResponse;
import com.bbva.entities.map_dependecy.MapDependencyEntity;
import com.bbva.entities.use_case_definition.UseCaseDefinitionEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface MapDependencyMapper {

    @Select({"SELECT md.map_dependency_id, md.use_case_id, md.key_data_process_type, pt.element_name as key_data_process_name, md.process_name, " +
            "md.slo_owner_id, slo.full_name, slo.employee_id, md.aris_code, md.dependency_map_link " +
            "FROM data_use_case_definition cd " +
            "INNER JOIN data_map_dependency md on cd.use_case_id = md.use_case_id " +
            "INNER JOIN catalog pt on md.key_data_process_type = pt.element_id and pt.catalog_id = 1028 " +
            "LEFT JOIN secu_user slo on md.slo_owner_id = slo.user_id " +
            "WHERE cd.project_id = #{projectId}"})
    @Results({
            @Result(property = "mapDependencyId", column = "map_dependency_id"),
            @Result(property = "useCaseId", column = "use_case_id"),
            @Result(property = "keyDataProcessType", column = "key_data_process_type"),
            @Result(property = "keyDataProcessName", column = "key_data_process_name"),
            @Result(property = "processName", column = "process_name"),
            @Result(property = "sloOwnerId", column = "slo_owner_id"),
            @Result(property = "sloOwnerName", column = "full_name"),
            @Result(property = "sloOwnerCode", column = "employee_id"),
            @Result(property = "arisCode", column = "aris_code"),
            @Result(property = "dependencyMapLink", column = "dependency_map_link"),
    })
    List<MapDependencyListByProjectResponse> listMapDependencyByProjectById(@Param("projectId") int projectId);

    @Insert("INSERT INTO  data_map_dependency(use_case_id, key_data_process_type, process_name_type, slo_owner_id, aris_code, dependency_map_link) " +
            "VALUES (#{useCaseId},#{keyDataProcessType},#{processNameType}, #{sloOwnerId}, #{arisCode}, #{dependencyMapLink})")
    @Options(useGeneratedKeys = true, keyProperty = "mapDependencyId", keyColumn = "map_dependency_id")
    boolean insert(MapDependencyEntity entity);

    @Update("UPDATE data_map_dependency \n" +
            "SET key_data_process_type = #{keyDataProcessType}, process_name_type = #{processNameType}, slo_owner_id = #{sloOwnerId},\n" +
            "aris_code = #{arisCode}, dependency_map_link = #{dependencyMapLink}\n" +
            "WHERE map_dependency_id = #{mapDependencyId}")
    boolean update(MapDependencyEntity entity);
}
