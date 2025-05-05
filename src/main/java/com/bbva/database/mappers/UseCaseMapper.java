package com.bbva.database.mappers;

import com.bbva.dto.use_case.request.UpdateOrInsertUseCaseDtoRequest;
import com.bbva.dto.use_case.response.UseCaseInputsDtoResponse;
import com.bbva.dto.use_case.response.UseCaseInputsFilterDtoResponse;
import com.bbva.entities.UpdateEntity;
import com.bbva.entities.UpdateOrInsertEntity;
import com.bbva.entities.usecase.UseCaseEntity;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UseCaseMapper {
    @Select({"<script>" +
            "SELECT use_case_id as useCaseId,use_case_name as useCaseName,use_case_description as useCaseDescription FROM use_case " +
            "where use_case_id != 9999 " +
            "</script>"})
    List<UseCaseEntity> listAllUseCases();

    @Select("CALL SP_INSERT_OR_UPDATE_USE_CASE(" +
            "#{useCaseId}," +
            "#{useCaseName}," +
            "#{useCaseDescription}," +
            "#{domainId})")
    @Results({
            @Result(property = "last_updated_id", column = "last_updated_id"),
            @Result(property = "updated_register", column = "updated_register"),
            @Result(property = "last_insert_id", column = "last_insert_id"),
            @Result(property = "new_register", column = "new_register")
    })
    UpdateOrInsertEntity updateOrInsertUseCase(UpdateOrInsertUseCaseDtoRequest dto);

    @Select("CALL SP_GET_FILTERED_USE_CASES(" +
            "#{domainName}," +
            "#{projectName})")
    @Results({
            @Result(property = "domainName", column = "domain_name"),
            @Result(property = "useCaseName", column = "use_case_name"),
            @Result(property = "useCaseDescription", column = "use_case_description"),
            @Result(property = "projectCount", column = "project_count"),
            @Result(property = "projects", column = "projects")
    })
    List<UseCaseInputsDtoResponse> getFilteredUseCases(String domainName, String projectName);
}
