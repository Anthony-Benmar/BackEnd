package com.bbva.database.mappers;

import com.bbva.dto.reliability.request.InventoryJobUpdateDtoRequest;
import com.bbva.dto.reliability.response.ExecutionValidationDtoResponse;
import com.bbva.dto.reliability.response.InventoryInputsDtoResponse;
import org.apache.ibatis.annotations.*;
import com.bbva.dto.reliability.response.PendingCustodyJobsDtoResponse;
import com.bbva.dto.reliability.response.ProjectCustodyInfoDtoResponse;

import java.util.List;

public interface ReliabilityMapper {

    @Select("CALL SP_LIST_INVENTORY_RELIABILITY(" +
            "#{domainName}," +
            "#{useCase}," +
            "#{jobType}," +
            "#{frequency}," +
            "#{isCritical}," +
            "#{searchByInputOutputTable})"
    )

    @Result(property = "domainName", column = "domain_name")
    @Result(property = "useCase", column = "use_case")
    @Result(property = "jobName", column = "job_name")
    @Result(property = "componentName", column = "component_name")
    @Result(property = "jobType", column = "job_type")
    @Result(property = "isCritical", column = "is_critical")
    @Result(property = "frequency", column = "frequency")
    @Result(property = "inputPaths", column = "input_paths")
    @Result(property = "outputPath", column = "output_path")
    @Result(property = "jobPhase", column = "job_phase")
    @Result(property = "domainId", column = "domain_id")
    @Result(property = "useCaseId", column = "use_case_id")
    @Result(property = "frequencyId", column = "frequency_id")
    @Result(property = "jobTypeId", column = "job_type_id")
    @Result(property = "bitBucketUrl", column = "bitbucket_url")
    @Result(property = "pack", column = "pack")
    List<InventoryInputsDtoResponse> inventoryInputsFilter(@Param("domainName") String domainName,
                                                              @Param("useCase") String useCase,
                                                              @Param("jobType") String jobType,
                                                              @Param("frequency") String frequency,
                                                              @Param("isCritical") String isCritical,
                                                              @Param("searchByInputOutputTable") String searchByInputOutputTable
    );
    @Update("CALL SP_UPDATE_INVENTORY_JOB_STOCK(" +
            "#{jobName}," +
            "#{componentName}," +
            "#{frequencyId}," +
            "#{inputPaths}," +
            "#{outputPath}," +
            "#{jobTypeId}," +
            "#{useCaseId}," +
            "#{isCritical}," +
            "#{domainId})")
    void updateInventoryJobStock(InventoryJobUpdateDtoRequest dto);

    @Select("CALL SP_GET_PENDING_CUSTODY_JOBS(#{sdatoolId})")

    @Result(property = "jobName", column = "job_name")
    @Result(property = "jsonName", column = "json_name")
    @Result(property = "frequency", column = "frequency")
    @Result(property = "jobType", column = "job_type")
    @Result(property = "originType", column = "origin_type")
    @Result(property = "phaseType", column = "phase_type")
    List<PendingCustodyJobsDtoResponse> getPendingCustodyJobs(@Param("sdatoolId") String sdatoolId);

    @Select("CALL SP_GET_PROJECT_CUSTODY_INFO(#{sdatoolId})")

    @Result(property = "useCase", column = "use_case")
    @Result(property = "pack", column = "pack")
    @Result(property = "domainName", column = "domain_name")
    @Result(property = "productOwner", column = "product_owner")
    List<ProjectCustodyInfoDtoResponse> getProjectCustodyInfo(@Param("sdatoolId") String sdatoolId);

    @Select("CALL SP_GET_EXECUTION_VALIDATION(#{jobName})")
    @Result(property = "validation", column = "validacion")
    ExecutionValidationDtoResponse getExecutionValidation(@Param("jobName") String jobName);
}
