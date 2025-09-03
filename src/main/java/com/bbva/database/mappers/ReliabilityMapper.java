package com.bbva.database.mappers;

import com.bbva.dto.catalog.response.DropDownDto;
import com.bbva.dto.reliability.request.*;
import com.bbva.dto.reliability.response.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ReliabilityMapper {

    @Select("CALL SP_LIST_INVENTORY_RELIABILITY(" +
            "#{c.domainName}," +
            "#{c.useCase}," +
            "#{c.jobType}," +
            "#{c.frequency}," +
            "#{c.isCritical}," +
            "#{c.searchByInputOutputTable}," +
            "#{c.searchType}," +
            "#{c.origin}" +
            ")")
    @Result(property = "domainName", column = "domain_name")
    @Result(property = "useCase", column = "use_case")
    @Result(property = "originTypeId", column = "origin_type_id")
    @Result(property = "origin", column = "origin")
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
    List<InventoryInputsDtoResponse> inventoryInputsFilter(
            @Param("c") InventoryInputsFilterDtoRequest c
    );

    @Select("""
            SELECT element_id AS value, element_name AS label
              FROM catalog
             WHERE catalog_id = 1003
               AND element_id <> 1003
             ORDER BY element_name
            """)
    @Result(property = "value", column = "value")
    @Result(property = "label", column = "label")
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
            "#{domainId}," +
            "#{exception}" +
            ")")
    void updateInventoryJobStock(InventoryJobUpdateDtoRequest dto);

    @Select("CALL SP_GET_PENDING_CUSTODY_JOBS(#{sdatoolId})")

    @Result(property = "jobName", column = "job_name")
    @Result(property = "jsonName", column = "json_name")
    @Result(property = "frequencyId", column = "frequency_id")
    @Result(property = "jobTypeId", column = "job_type_id")
    @Result(property = "originTypeId", column = "origin_type_id")
    @Result(property = "phaseTypeId", column = "phase_type_id")
    @Result(property = "principalJob", column = "principal_job")
    @Result(property = "status", column = "status")
    List<PendingCustodyJobsDtoResponse> getPendingCustodyJobs(@Param("sdatoolId") String sdatoolId);

    @Select("""
            SELECT period, execution_status FROM ( SELECT je.period, je.execution_status,
                CASE 
                    WHEN j.folder LIKE '%DIA%' OR j.folder LIKE '%DAY%' THEN 7
                    ELSE 1
                  END AS keep_n,
                  ROW_NUMBER() OVER ( PARTITION BY je.job_name ORDER BY je.period DESC, je.end_time DESC) AS rn
                FROM job_execution je
                JOIN job j ON j.job_name = je.job_name
                WHERE je.job_name = #{jobName}
            ) t
            WHERE t.rn <= t.keep_n ORDER BY period DESC
            """)
    @Result(property = "period", column = "period")
    @Result(property = "executionStatus", column = "execution_status")
    List<JobExecutionHistoryDtoResponse> getJobExecutionHistory(@Param("jobName") String jobName);


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
            "#{productOwnerEmail}," +
            "#{useCaseId}," +
            "#{projectId}," +
            "#{creatorUserId}," +
            "#{pdfLink}," +
            "#{jobCount}," +
            "#{statusId}," +
            "#{sn2}" +
            ")")
    void insertTranfer(TransferInputDtoRequest dto);

    @Select("""
            SELECT element_id AS value, element_desc AS rawDesc FROM catalog WHERE catalog_id = 9006 AND status_type = 1 AND element_name = (SELECT element_desc FROM catalog
                 WHERE catalog_id = 1027 AND element_id = #{sn1}) ORDER BY element_desc
            """)
    @Result(property = "value", column = "value")
    @Result(property = "rawDesc", column = "rawDesc")
    List<RawSn2DtoResponse> fetchRawSn2BySn1(@Param("sn1") Integer sn1);

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
            "#{statusId}," +
            "#{exception}" +
            ")")
    void insertJobStock(JobTransferInputDtoRequest dto);

    @Update("""
        UPDATE job_stock SET
        component_name = COALESCE(#{componentName}, component_name),
        frequency_id   = COALESCE(#{frequencyId},   frequency_id),
        input_paths    = COALESCE(#{inputPaths},    input_paths),
        output_path    = COALESCE(#{outputPath},    output_path),
        job_type_id    = COALESCE(#{jobTypeId},     job_type_id),
        use_case_id    = COALESCE(#{useCaseId},     use_case_id),
        is_critical    = COALESCE(#{isCritical},    is_critical),
        domain_id      = COALESCE(#{domainId},      domain_id),
        bitbucket_url  = COALESCE(#{bitBucketUrl},  bitbucket_url),
        responsible    = COALESCE(#{responsible},   responsible),
        job_phase_id   = COALESCE(#{jobPhaseId},    job_phase_id),
        origin_type_id = COALESCE(#{originTypeId},  origin_type_id),
        exception      = COALESCE(#{exception},     exception),
        comments       = COALESCE(#{comments},      comments)
        WHERE pack = #{pack} AND job_name = #{jobName}
        """)
    int updateJobByPackAndName(UpdateJobDtoRequest dto);

    @Update("UPDATE reliability_packs SET comments = #{comments} WHERE pack = #{pack}")
    int updatePackComments(@Param("pack") String pack, @Param("comments") String comments);

    @Select("SELECT status_id FROM reliability_packs WHERE pack = #{pack} LIMIT 1")
    Integer getPackStatus(@Param("pack") String pack);

    @Update("UPDATE reliability_packs SET domain_id   = COALESCE(#{domainId},  domain_id), use_case_id = COALESCE(#{useCaseId}, use_case_id), comments    = COALESCE(#{comments},  comments) WHERE pack = #{pack}")
    int patchPackHeader(@Param("pack") String pack, @Param("domainId") Integer domainId, @Param("useCaseId") Integer useCaseId, @Param("comments") String comments);

    @Update("UPDATE job_stock SET comments = #{comments} WHERE pack = #{pack} AND job_name = #{jobName}")
    int updateJobComment(@Param("pack") String pack, @Param("jobName") String jobName, @Param("comments") String comments);

    @Select("SELECT c.element_name FROM reliability_km_access a JOIN catalog c ON c.catalog_id=1027 AND c.element_id=a.domain_id WHERE a.status_type=1 AND LOWER(a.email)=LOWER(#{email}) ORDER BY c.element_name")
    List<String> getKmAllowedDomainNames(@Param("email") String email);

    @Update("UPDATE reliability_packs SET status_id = #{estado} WHERE pack = #{pack}")
    void updateReliabilityStatus(@Param("pack") String pack, @Param("estado") int estado);

    @Update("UPDATE job_stock SET status_id = #{estado} WHERE pack = #{pack}")
    void updateProjectInfoStatus(@Param("pack") String pack, @Param("estado") int estado);

    @Select("CALL SP_LIST_TRANSFERS_BY_STATUS(" +
            "#{domainName}," +
            "#{useCase}," +
            "#{statusList})"
    )
    @Result(property = "pack", column = "pack")
    @Result(property = "domainId", column = "domainId")
    @Result(property = "domainName", column = "domain_name")
    @Result(property = "productOwnerEmail",  column = "product_owner_email")
    @Result(property = "useCaseId", column = "useCaseId")
    @Result(property = "useCase", column = "use_case")
    @Result(property = "projectId", column = "projectId")
    @Result(property = "sdaToolId", column = "sdaToolId")
    @Result(property = "creatorUser", column = "creatorUser")
    @Result(property = "pdfLink", column = "pdfLink")
    @Result(property = "jobCount", column = "jobCount")
    @Result(property = "statusId", column = "statusId")
    @Result(property = "statusName", column = "status_name")
    List<ReliabilityPacksDtoResponse> listTransfersByStatus(
            @Param("domainName") String domainNameCsv,
            @Param("useCase") String useCaseCsv,
            @Param("statusList") String statusListCsv
    );

    @Select("CALL SP_GET_TRANSFER_HEADER(#{pack})")
    @Result(property = "pack",              column = "pack")
    @Result(property = "sdaToolId",         column = "sdaToolId")
    @Result(property = "domainId",          column = "domainId")
    @Result(property = "domainName",        column = "domain_name")
    @Result(property = "useCaseId",         column = "useCaseId")
    @Result(property = "useCase",           column = "use_case")
    @Result(property = "statusId",          column = "statusId")
    @Result(property = "statusName",        column = "status_name")
    @Result(property = "comments",          column = "comments")
    @Result(property = "creatorEmail",      column = "creator_email")
    @Result(property = "pdfLink",           column = "pdf_link")
    @Result(property = "sn2Id",             column = "sn2_id")
    @Result(property = "sn2Desc",           column = "sn2_desc")
    @Result(property = "productOwnerEmail", column = "product_owner_email")
    TransferDetailResponse.Header getTransferHeader(@Param("pack") String pack);

    @Select("""
        SELECT
            job_name       AS jobName, component_name AS jsonName,
            frequency_id   AS frequencyId, job_type_id    AS jobTypeId,
            job_phase_id   AS jobPhaseId, origin_type_id AS originTypeId,
            input_paths    AS inputPaths, output_path    AS outputPath,
            bitbucket_url  AS bitBucketUrl, responsible    AS responsible,
            use_case_id    AS useCaseId, domain_id      AS domainId,
            is_critical    AS isCritical, status_id      AS statusId,
            comments       AS comments
        FROM job_stock WHERE pack = #{pack} ORDER BY job_name
        """)
    List<TransferDetailResponse.JobRow> getTransferJobs(@Param("pack") String pack);
}
