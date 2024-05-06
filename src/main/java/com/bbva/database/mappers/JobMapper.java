package com.bbva.database.mappers;

import com.bbva.dto.job.request.JobDTO;
import com.bbva.dto.job.request.JobAdditionalDtoRequest;
import com.bbva.dto.job.request.JobMonitoringDtoRequest;
import com.bbva.dto.job.request.JobMonitoringRequestInsertDtoRequest;
import com.bbva.dto.job.request.JobMonitoringRequestFilterDtoRequest;
import com.bbva.dto.job.response.*;
import com.bbva.entities.InsertEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.*;

import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import java.util.Date;
import java.util.List;

public interface JobMapper {
    String SQL_QUERY_JOB = "SELECT * FROM job_basic_info";

    @Results({
            @Result(property = "jobId", column = "job_id"),
            @Result(property = "jobName", column = "job_name"),
            @Result(property = "folderName", column = "folder_name"),
            @Result(property = "applicationName", column = "application_name"),
            @Result(property = "subApplicationName", column = "sub_application_name"),
            @Result(property = "runAsName", column = "run_as_name"),
            @Result(property = "nodeId", column = "node_id"),
            @Result(property = "maxWaitNumber", column = "max_wait_number"),
            @Result(property = "cmdLine", column = "cmd_line"),
            @Result(property = "createAuditDate", column = "create_audit_date"),
            @Result(property = "createAuditUser", column = "create_audit_user"),
            @Result(property = "updateAuditDate", column = "update_audit_date"),
            @Result(property = "updateAuditUser", column = "update_audit_user")
    })

    @Select(SQL_QUERY_JOB)
    List<JobBasicInfoDtoResponse> listAll();


    @Select("CALL SP_LIST_JOB(" +
            "#{domainId}," +
            "#{projectId}," +
            "#{jobDataprocFolderName}," +
            "#{classificationType}," +
            "#{invetoriedType})")
    @Results({
            @Result(property = "jobId", column = "job_id"),
            @Result(property = "jobName", column = "job_name"),
            @Result(property = "folderName", column = "folder_name"),
            @Result(property = "applicationName", column = "application_name"),
            @Result(property = "dataprocJobName", column = "dataproc_job_name"),
            @Result(property = "runAsName", column = "run_as_name"),
            @Result(property = "classificationType", column = "classification_type"),
            @Result(property = "classificationName", column = "classification_name"),
            @Result(property = "invetoriedType", column = "invetoried_type"),
            @Result(property = "monitoringDomainId", column = "monitoring_domain_id"),
            @Result(property = "monitoringDomainName", column = "monitoring_domain_name"),
            @Result(property = "monitoringProjectId", column = "monitoring_project_id"),
            @Result(property = "monitoringProjectName", column = "monitoring_project_name"),
            @Result(property = "monitoringDevEmail", column = "monitoring_dev_email"),
            @Result(property = "subApplicationName", column = "sub_application_name"),
            @Result(property = "flagCriticalRoute", column = "flag_critical_route")
    })

    List<JobBasicInfoSelectDtoResponse> jobBasicInfoFilter(@Param("domainId") int domainId,
                                                           @Param("projectId") int projectId,
                                                           @Param("jobDataprocFolderName") String jobDataprocFolderName,
                                                           @Param("classificationType") int classificationType,
                                                           @Param("invetoriedType") int invetoriedType);


    @Select("CALL SP_GET_JOB_BY_ID(#{jobId})")
    @Results({
            @Result(property = "jobId", column = "job_id"),
            @Result(property = "jobName", column = "job_name"),
            @Result(property = "folderName", column = "folder_name"),
            @Result(property = "applicationName", column = "application_name"),
            @Result(property = "subApplicationName", column = "sub_application_name"),
            @Result(property = "dataprocJobName", column = "dataproc_job_name"),
            @Result(property = "runAsName", column = "run_as_name"),
            @Result(property = "nodeId", column = "node_id"),
            @Result(property = "maxWaitNumber", column = "max_wait_number"),
            @Result(property = "cmdLineDesc", column = "cmd_line_desc"),
            @Result(property = "classificationType", column = "classification_type"),
            @Result(property = "classificationName", column = "classification_name"),
            @Result(property = "monitoringDomainId", column = "monitoring_domain_id"),
            @Result(property = "monitoringDomainName", column = "monitoring_domain_name"),
            @Result(property = "monitoringProjectId", column = "monitoring_project_id"),
            @Result(property = "monitoringProjectName", column = "monitoring_project_name"),
            @Result(property = "monitoringDevEmail", column = "monitoring_dev_email"),
            @Result(property = "createdDomainId", column = "created_domain_id"),
            @Result(property = "createdDomainName", column = "created_domain_name"),
            @Result(property = "createdProjectId", column = "created_project_id"),
            @Result(property = "createdProjectName", column = "created_project_name"),
            @Result(property = "createdDevEmail", column = "created_dev_email"),
            @Result(property = "criticalRouteType", column = "critical_route_type"),
            @Result(property = "jobFunctionalDesc", column = "job_functional_desc"),
            @Result(property = "createAuditDate", column = "create_audit_date"),
            @Result(property = "createAuditUser", column = "create_audit_user"),
            @Result(property = "updateAuditDate", column = "update_audit_date"),
            @Result(property = "updateAuditUser", column = "update_audit_user")
        })
        JobDTO getJobById(@Param("jobId") int jobId);

    @Select("CALL SP_GET_JOB_BY_ID(#{jobId})")
    @Results({
            @Result(property = "jobId", column = "job_id"),
            @Result(property = "jobName", column = "job_name"),
            @Result(property = "folderName", column = "folder_name"),
            @Result(property = "applicationName", column = "application_name"),
            @Result(property = "subApplicationName", column = "sub_application_name"),
            @Result(property = "dataprocJobName", column = "dataproc_job_name"),
            @Result(property = "runAsName", column = "run_as_name"),
            @Result(property = "nodeId", column = "node_id"),
            @Result(property = "maxWaitNumber", column = "max_wait_number"),
            @Result(property = "cmdLineDesc", column = "cmd_line_desc"),
            @Result(property = "classificationType", column = "classification_type"),
            @Result(property = "classificationName", column = "classification_name"),
            @Result(property = "monitoringDomainId", column = "monitoring_domain_id"),
            @Result(property = "monitoringDomainName", column = "monitoring_domain_name"),
            @Result(property = "monitoringProjectId", column = "monitoring_project_id"),
            @Result(property = "monitoringProjectName", column = "monitoring_project_name"),
            @Result(property = "monitoringDevEmail", column = "monitoring_dev_email"),
            @Result(property = "createdDomainId", column = "created_domain_id"),
            @Result(property = "createdDomainName", column = "created_domain_name"),
            @Result(property = "createdProjectId", column = "created_project_id"),
            @Result(property = "createdProjectName", column = "created_project_name"),
            @Result(property = "createdDevEmail", column = "created_dev_email"),
            @Result(property = "createAuditDate", column = "create_audit_date"),
            @Result(property = "createAuditUser", column = "create_audit_user"),
            @Result(property = "updateAuditDate", column = "update_audit_date"),
            @Result(property = "updateAuditUser", column = "update_audit_user")
    })
    JobBasicInfoByIdDtoResponse jobBasicDetail(@Param("jobId") int jobId);


    @Select("CALL SP_GET_JOB_ADDITIONAL(" +
            "#{jobId})")
    @Results({
            @Result(property = "jobId", column = "job_id"),
            @Result(property = "classificationType", column = "reclassification_type"),
            @Result(property = "criticalRouteType", column = "critical_route_type"),
            @Result(property = "jobFunctionalDesc", column = "job_functional_desc"),
    })
    JobAdditionalDtoResponse getAdditional (@Param("jobId") Integer jobId);


    @Select("CALL SP_INSERT_OR_UPDATE_JOB_ADDITIONAL(" +
            "#{jobId}," +
            "#{createdProjectId}," +
            "#{createdDevEmail}," +
            "#{monitoringProjectId}," +
            "#{monitoringDevEmail}," +
            "#{classificationType}," +
            "#{criticalRouteType}," +
            "#{jobFunctionalDesc}," +
            "#{createAuditUser})")
    @Results({
            @Result(property = "jobId", column = "job_id"),
            @Result(property = "createdProjectId", column = "created_project_id"),
            @Result(property = "createdDevEmail", column = "created_dev_email"),
            @Result(property = "monitoringProjectId", column = "monitoring_project_id"),
            @Result(property = "monitoringDevEmail", column = "monitoring_dev_email"),
            @Result(property = "classificationType", column = "reclassification_type"),
            @Result(property = "criticalRouteType", column = "critical_route_type"),
            @Result(property = "jobFunctionalDesc", column = "job_functional_desc"),
            @Result(property = "createAuditUser", column = "create_audit_user"),
            @Result(property = "createAuditDate", column = "create_audit_date"),
            @Result(property = "updateAuditDate", column = "update_audit_date"),
            @Result(property = "updateAuditUser", column = "update_audit_user")
    })
    JobAdditionalDtoResponse updateAdditional (JobAdditionalDtoRequest dto);

    List<JobMonitoringDtoResponse> getAllMonitoringRequest();

    @Select("CALL SP_FILTER_MONITORING_REQUEST(" +
            "#{domainId}," +
            "#{toSdatoolId}," +
            "#{jobId}," +
            "#{statusType})")
    @Results({
            @Result(property = "monitoringRequestId", column = "monitoring_request_id"),
            @Result(property = "jobId", column = "job_id"),
            @Result(property = "fromSdatoolId", column = "from_sdatool_id"),
            @Result(property = "fromDevEmail", column = "from_dev_email"),
            @Result(property = "toSdatoolId", column = "to_sdatool_id"),
            @Result(property = "toDevEmail", column = "to_dev_email"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "statusType", column = "status_type"),
            @Result(property = "commentRequestDesc", column = "comment_request_desc"),
            @Result(property = "createAuditDate", column = "create_audit_date"),
            @Result(property = "createAuditUser", column = "create_audit_user"),
            @Result(property = "updateAuditDate", column = "update_audit_date"),
            @Result(property = "updateAuditUser", column = "update_audit_user")
    })
    List<JobMonitoringRequestSelectDtoResponse> filterMonitoringRequest(
            @Param("domainId") Integer domainId,
            @Param("toSdatoolId") String toSdatoolId,
            @Param("jobId") Integer jobId,
            @Param("statusType") Integer statusType);

    @Select("CALL SP_INSERT_MONITORING_REQUEST(" +
            "#{jobId}, " +
            "#{fromSdatoolId}, " +
            "#{fromDevEmail}, " +
            "#{toSdatoolId}, " +
            "#{toDevEmail}, " +
            "#{startDate}, " +
            "#{endDate}, " +
            "#{statusType}, " +
            "#{commentRequestDesc}, " +
            "#{createAuditUser})")
    @Results({
            @Result(property = "last_insert_id", column = "last_insert_id"),
            @Result(property = "new_register", column = "new_register")
    })
    InsertEntity insertMonitoringRequest(JobMonitoringRequestInsertDtoRequest dto);


    @Select("CALL SP_UPDATE_MONITORING_REQUEST(" +
            "#{monitoringRequestId}, " +
            "#{jobId}, " +
            "#{fromSdatoolId}, " +
            "#{fromDevEmail}, " +
            "#{toSdatoolId}, " +
            "#{toDevEmail}, " +
            "#{startDate}, " +
            "#{endDate}, " +
            "#{statusType}, " +
            "#{commentRequestDesc}, " +
            "#{updateAuditUser})")
    @Results({
            @Result(property = "monitoringRequestId", column = "monitoring_request_id"),
            @Result(property = "jobId", column = "job_id"),
            @Result(property = "fromSdatoolId", column = "from_sdatool_id"),
            @Result(property = "fromDevEmail", column = "from_dev_email"),
            @Result(property = "toSdatoolId", column = "to_sdatool_id"),
            @Result(property = "toDevEmail", column = "to_dev_email"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "statusType", column = "status_type"),
            @Result(property = "commentRequestDesc", column = "comment_request_desc"),
            @Result(property = "updateAuditUser", column = "update_audit_user")
    })
    void updateMonitoringRequest(JobMonitoringDtoRequest dto);

}
