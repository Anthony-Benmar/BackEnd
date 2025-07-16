package com.bbva.database.mappers;

import com.bbva.dto.catalog.response.DropDownDto;
import com.bbva.dto.reliability.request.InventoryJobUpdateDtoRequest;
import com.bbva.dto.reliability.request.JobTransferInputDtoRequest;
import com.bbva.dto.reliability.request.TransferInputDtoRequest;
import com.bbva.dto.reliability.response.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ReliabilityMapper {

    @Select("CALL SP_LIST_INVENTORY_RELIABILITY(" +
            "#{domainName}," +
            "#{useCase}," +
            "#{jobType}," +
            "#{frequency}," +
            "#{isCritical}," +
            "#{searchByInputOutputTable}, "+
            "#{searchType}," +
            "#{origin})"
    )

    @Result(property = "domainName", column = "domain_name")
    @Result(property = "useCase", column = "use_case")
    @Result(property = "originTypeId",   column = "origin_type_id")
    @Result(property = "origin",       column = "origin")
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
                                                              @Param("searchByInputOutputTable") String searchByInputOutputTable,
                                                              @Param("searchType") String searchType,
                                                              @Param("origin") String origin
    );

    @Select(
            "SELECT element_id AS value, element_name AS label\n" +
                    "  FROM catalog\n" +
                    " WHERE catalog_id = 1003\n" +
                    "   AND element_id <> 1003\n" +
                    " ORDER BY element_name"
    )
    @Results({
            @Result(property = "value", column = "value"),
            @Result(property = "label", column = "label")
    })
    List<DropDownDto> getOriginTypes();

    @Update("CALL SP_UPDATE_INVENTORY_JOB_STOCK(" +
            "#{jobName}," +
            "#{componentName}," +
            "#{bitBucketUrl}," +
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
    @Result(property = "frequencyId", column = "frequency_id")
    @Result(property = "jobTypeId", column = "job_type_id")
        @Result(property = "originTypeId", column = "origin_type_id")
    @Result(property = "phaseTypeId", column = "phase_type_id")
    @Result(property = "principalJob", column = "principal_job")
    List<PendingCustodyJobsDtoResponse> getPendingCustodyJobs(@Param("sdatoolId") String sdatoolId);

    @Select("CALL SP_GET_PROJECT_CUSTODY_INFO(#{sdatoolId})")

    @Result(property = "useCase", column = "use_case")
    @Result(property = "useCaseId", column = "use_case_id")
    @Result(property = "domainId", column = "domain_id")
    @Result(property = "pack", column = "pack")
    @Result(property = "domainName", column = "domain_name")
    @Result(property = "productOwnerUserId", column = "participant_id")
    @Result(property = "productOwner", column = "product_owner")
    List<ProjectCustodyInfoDtoResponse> getProjectCustodyInfo(@Param("sdatoolId") String sdatoolId);

    @Select("CALL SP_GET_RELIABILITY_PACKS(" +
            "#{domainName}," +
            "#{useCase})"
    )
    @Result(property = "pack", column = "pack")
    @Result(property = "domainId", column = "domainId")
    @Result(property = "domainName", column = "domain_name")
    @Result(property = "productOwnerUserId", column = "productOwnerUserId")
    @Result(property = "useCaseId", column = "useCaseId")
    @Result(property = "useCase", column = "use_case")
    @Result(property = "projectId", column = "projectId")
    @Result(property = "sdaToolId", column = "sdaToolId")
    @Result(property = "creatorUserId", column = "creatorUserId")
    @Result(property = "pdfLink", column = "pdfLink")
    @Result(property = "jobCount", column = "jobCount")
    List<ReliabilityPacksDtoResponse> getReliabilityPacks(@Param("domainName") String domainName,
                                                          @Param("useCase") String useCase);

    @Select("CALL SP_GET_EXECUTION_VALIDATION(#{jobName})")
    @Result(property = "validation", column = "validacion")
    ExecutionValidationDtoResponse getExecutionValidation(@Param("jobName") String jobName);

    @Update("CALL SP_INSERT_RELIABILITY_PACK(" +
            "#{pack}," +
            "#{domainId}," +
            "#{productOwnerUserId}," +
            "#{useCaseId}," +
            "#{projectId}," +
            "#{creatorUserId}," +
            "#{pdfLink}," +
            "#{jobCount}," +
            "#{statusId}" +
            ")")
    void insertTranfer(TransferInputDtoRequest dto);

    @Insert("CALL SP_INSERT_JOB_STOCK(" +
            "#{jobTypeId}," +
            "#{jobName}," +
            "#{componentName}," +
            "#{frequencyId}," +
            "#{bitbucketUrl}," +
            "#{inputPaths}," +
            "#{outputPath}," +
            "#{responsible}," +
            "#{comments}," +
            "#{jobPhaseId}," +
            "#{originTypeId}," +
            "#{useCaseId}," +
            "#{isCritical}," +
            "#{domainId}," +
            "#{pack}," +
            "#{statusId}" +
            ")")
    void insertJobStock(JobTransferInputDtoRequest dto);

    @Update("UPDATE reliability_packs SET status_id = #{estado} WHERE pack = #{pack}")
    void updateReliabilityStatus(@Param("pack") String pack, @Param("estado") int estado);

    @Update("UPDATE job_stock SET status_id = #{estado} WHERE pack = #{pack}")
    void updateProjectInfoStatus(@Param("pack") String pack, @Param("estado") int estado);
}
