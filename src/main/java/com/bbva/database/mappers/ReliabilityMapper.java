package com.bbva.database.mappers;

import com.bbva.dto.reliability.response.ExecutionValidationDtoResponse;
import com.bbva.dto.reliability.response.InventoryInputsDtoResponse;
import com.bbva.dto.reliability.response.PendingCustodyJobsDtoResponse;
import com.bbva.dto.reliability.response.ProjectCustodyInfoDtoResponse;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ReliabilityMapper {

    @Select("CALL SP_LIST_INVENTORY_RELIABILITY(" +
            "#{domainName}," +
            "#{useCase}," +
            "#{jobType}," +
            "#{frequency}," +
            "#{isCritical}," +
            "#{searchByInputOutputTable})")

    @Results({
            @Result(property = "domainName", column = "domain_name"),
            @Result(property = "useCase", column = "use_case"),
            @Result(property = "jobName", column = "job_name"),
            @Result(property = "componentName", column = "component_name"),
            @Result(property = "jobType", column = "job_type"),
            @Result(property = "isCritical", column = "is_critical"),
            @Result(property = "frequency", column = "frequency"),
            @Result(property = "inputPaths", column = "input_paths"),
            @Result(property = "outputPath", column = "output_path"),
            @Result(property = "jobPhase", column = "job_phase")
    })
    List<InventoryInputsDtoResponse> inventoryInputsFilter(@Param("domainName") String domainName,
                                                              @Param("useCase") String useCase,
                                                              @Param("jobType") String jobType,
                                                              @Param("frequency") String frequency,
                                                              @Param("isCritical") String isCritical,
                                                              @Param("searchByInputOutputTable") String searchByInputOutputTable
    );
    @Select("CALL SP_GET_PENDING_CUSTODY_JOBS(#{sdatoolId})")
    @Results({
            @Result(property = "jobName", column = "job_name"),
            @Result(property = "jsonName", column = "json_name"),
            @Result(property = "frequency", column = "frequency"),
            @Result(property = "jobType", column = "job_type"),
            @Result(property = "originType", column = "origin_type"),
            @Result(property = "phaseType", column = "phase_type")
    })
    List<PendingCustodyJobsDtoResponse> getPendingCustodyJobs(@Param("sdatoolId") String sdatoolId);

    @Select("CALL SP_GET_PROJECT_CUSTODY_INFO(#{sdatoolId})")
    @Results({
            @Result(property = "useCase", column = "use_case"),
            @Result(property = "pack", column = "pack"),
            @Result(property = "domainName", column = "domain_name"),
            @Result(property = "productOwner", column = "product_owner")
    })
    List<ProjectCustodyInfoDtoResponse> getProjectCustodyInfo(@Param("sdatoolId") String sdatoolId);

    @Select("CALL SP_GET_EXECUTION_VALIDATION(#{jobName})")
    @Results({
            @Result(property = "validation", column = "validacion")
    })
    ExecutionValidationDtoResponse getExecutionValidation(@Param("jobName") String jobName);
}
