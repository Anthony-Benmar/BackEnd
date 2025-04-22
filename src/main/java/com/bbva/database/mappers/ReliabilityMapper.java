package com.bbva.database.mappers;

import com.bbva.dto.reliability.response.InventoryInputsDtoResponse;
import org.apache.ibatis.annotations.*;

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
            @Result(property = "jobPhase", column = "job_phase"),
            @Result(property = "domainId", column = "domain_id"),
            @Result(property = "useCaseId", column = "use_case_id"),
            @Result(property = "frequencyId", column = "frequency_id"),
            @Result(property = "jobTypeId", column = "job_type_id")
    })
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
    void updateInventoryJobStock(
            @Param("jobName") String jobName,
            @Param("componentName") String componentName,
            @Param("frequencyId") int frequencyId,
            @Param("inputPaths") String inputPaths,
            @Param("outputPath") String outputPath,
            @Param("jobTypeId") int jobTypeId,
            @Param("useCaseId") int useCaseId,
            @Param("isCritical") String isCritical,
            @Param("domainId") int domainId
    );
}
