package com.bbva.database.mappers;

import com.bbva.dto.batch.request.InsertAJIFJobExecutionRequest;
import com.bbva.dto.batch.request.InsertCSATJobExecutionRequest;
import com.bbva.dto.batch.request.InsertReliabilityIncidenceDTO;
import com.bbva.dto.batch.response.JobExecutionByIdDTO;
import com.bbva.dto.batch.response.JobExecutionFilterData;
import com.bbva.dto.batch.response.StatusJobExecutionDTO;
import com.bbva.entities.InsertEntity;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

public interface BatchMapper {
    @Select("CALL SP_FILTER_JOB_EXECUTION(" +
            "#{pageCurrent}," +
            "#{recordsAmount}," +
            "#{jobName}," +
            "#{startDate}," +
            "#{endDate}," +
            "#{folder}," +
            "#{dataproc}," +
            "#{orderId}," +
            "#{projectName}," +
            "#{sdatoolId}," +
            "#{domain}, " +
            "#{isTypified})")
    @Results({
            @Result(property = "jobName", column = "job_name"),
            @Result(property = "folder", column = "folder"),
            @Result(property = "application", column = "application"),
            @Result(property = "subApplication", column = "sub_application"),
            @Result(property = "jsonName", column = "json_name"),
            @Result(property = "orderId", column = "order_id"),
            @Result(property = "orderDate", column = "order_date"),
            @Result(property = "startTime", column = "start_time"),
            @Result(property = "endTime", column = "end_time"),
            @Result(property = "executionStatus", column = "execution_status"),
            @Result(property = "projectName", column = "project_name"),
            @Result(property = "scrumTeam", column = "scrum_team"),
            @Result(property = "scrumMaster", column = "scrum_master"),
            @Result(property = "solutionDetail", column = "solution_detail"),
            @Result(property = "errorType", column = "error_type"),
            @Result(property = "errorDesc", column = "error_desc"),
            @Result(property = "errorReason", column = "error_reason"),
            @Result(property = "errorReasonDesc", column = "error_reason_desc"),
            @Result(property = "updatedBy", column = "updated_by"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "createdBy", column = "created_by"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "sdatoolId", column = "sdatool_id"),
            @Result(property = "domain", column = "service_owner"),
            @Result(property = "recordsCount", column = "records_count"),
            @Result(property = "isTypified", column = "isTypified"),
            @Result(property = "typified", column = "typified"),
            @Result(property = "withoutTypified", column = "withoutTypified"),
            @Result(property = "logArgos", column = "log_argos"),
            @Result(property = "runCounter", column = "run_counter"),
            @Result(property = "executionType", column = "execution_type"),
            @Result(property = "ticketJira", column = "ticket_jira")
    })
    List<JobExecutionFilterData> filter(@Param("pageCurrent") int page,
                                        @Param("recordsAmount") int recordsAmount,
                                        @Param("jobName") String jobName,
                                        @Param("startDate") String startDate,
                                        @Param("endDate") String endDate,
                                        @Param("folder") String folder,
                                        @Param("dataproc") String dataproc,
                                        @Param("orderId") String orderId,
                                        @Param("projectName") String projectName,
                                        @Param("sdatoolId") String sdatoolId,
                                        @Param("domain") String domain,
                                        @Param("isTypified") Boolean isTypified);

    @Select("CALL SP_INSERT_RELIABILITY_INCIDENCE_ACTION(" +
            "#{jobName}," +
            "#{orderDate}," +
            "#{orderId}," +
            "#{errorType}," +
            "#{errorReason}," +
            "#{solutionDetail}," +
            "#{employeeId}," +
            "#{logArgos}," +
            "#{runCounter}," +
            "#{ticketJira}," +

            "#{issueActionsId}," +
            "#{jobId}," +
            //"#{jobName}," +
            "#{folderName}," +
            "#{devEmail},"+
            "#{startDate}," +
            "#{endDate}," +
            "#{statusType}," +
            "#{commentActionsDesc}," +
            "#{createAuditUser}," +
            "#{updateAuditUser})")
    @Results({
            @Result(property = "last_insert_id", column = "last_insert_id"),
            @Result(property = "new_register", column = "new_register"),
            @Result(property = "last_insert_id_n", column = "last_insert_id_n"),
            @Result(property = "new_register_n", column = "new_register_n")
    })
    InsertEntity insertReliabilityIncidence(@Param("jobName") String jobName,
                                            @Param("orderDate") String orderDate,
                                            @Param("orderId") String orderId,
                                            @Param("errorType") Integer errorType,
                                            @Param("errorReason") Integer errorReason,
                                            @Param("solutionDetail") String solutionDetail,
                                            @Param("employeeId") String employeeId,
                                            @Param("logArgos") String logArgos,
                                            @Param("runCounter") Integer runCounter,
                                            @Param("ticketJira") String ticketJira,
                                            @Param("issueActionsId") Integer issueActionsId,
                                            @Param("jobId") Integer jobId,
                                            //@Param("jobName") String jobName,
                                            @Param("folderName") String folderName,
                                            @Param("devEmail") String devEmail,
                                            @Param("startDate") Date startDate,
                                            @Param("endDate") Date endDate,
                                            @Param("statusType") Integer statusType,
                                            @Param("commentActionsDesc") String commentActionsDesc,
                                            @Param("createAuditUser") String createAuditUser,
                                            @Param("updateAuditUser") String updateAuditUser);

    @Select("CALL SP_INSERT_JOB_EXECUTION_CSTAT("+
            "#{jobId},"+
            "#{jobName},"+
            "#{orderId},"+
            "#{folder},"+
            "#{application},"+
            "#{subApplication},"+
            "#{orderDate},"+
            "#{startTime},"+
            "#{endTime},"+
            "#{host},"+
            "#{runAs},"+
            "#{executionStatus},"+
            "#{sourceOrigin},"+
            "#{createDate},"+
            "#{updateDate})")
    @Results({
            @Result(property = "jobId", column = "job_id"),
            @Result(property = "jobName", column = "job_name"),
            @Result(property = "orderId", column = "order_id"),
            @Result(property = "folder", column = "folder"),
            @Result(property = "application", column = "application"),
            @Result(property = "subApplication", column = "sub_application"),
            @Result(property = "orderDate", column = "order_date"),
            @Result(property = "startTime", column = "start_time"),
            @Result(property = "endTime", column = "end_time"),
            @Result(property = "host", column = "host"),
            @Result(property = "runAs", column = "run_as"),
            @Result(property = "executionStatus", column = "execution_status"),
            @Result(property = "sourceOrigin", column = "source_origin"),
            @Result(property = "createDate", column = "create_date"),
            @Result(property = "updateDate", column = "source_origin"),
    })
    InsertEntity insertCSATJobExecution(InsertCSATJobExecutionRequest dto);

    @Select("CALL SP_INSERT_JOB_EXECUTION_AJIF("+
            "#{jobId},"+
            "#{jobName},"+
            "#{orderId},"+
            "#{folder},"+
            "#{application},"+
            "#{subApplication},"+
            "#{orderDate},"+
            "#{startTime},"+
            "#{endTime},"+
            "#{host},"+
            "#{runAs},"+
            "#{executionStatus},"+
            "#{sourceOrigin},"+
            "#{createDate},"+
            "#{updateDate})")
    @Results({
            @Result(property = "jobId", column = "job_id"),
            @Result(property = "jobName", column = "job_name"),
            @Result(property = "orderId", column = "order_id"),
            @Result(property = "folder", column = "folder"),
            @Result(property = "application", column = "application"),
            @Result(property = "subApplication", column = "sub_application"),
            @Result(property = "orderDate", column = "order_date"),
            @Result(property = "startTime", column = "start_time"),
            @Result(property = "endTime", column = "end_time"),
            @Result(property = "host", column = "host"),
            @Result(property = "runAs", column = "run_as"),
            @Result(property = "executionStatus", column = "execution_status"),
            @Result(property = "sourceOrigin", column = "source_origin"),
            @Result(property = "createDate", column = "create_date"),
            @Result(property = "updateDate", column = "source_origin"),
    })
    InsertEntity insertAJIFJobExecution(InsertAJIFJobExecutionRequest dto);

    @Select("CALL SP_GET_STATUS_JOB_EXECUTION(" +
            "#{jobName}," +
            "#{quantity})")
    @Results({
            @Result(property = "jobName", column = "job_name"),
            @Result(property = "orderId", column = "order_id"),
            @Result(property = "orderDate", column = "order_date"),
            @Result(property = "runCounter", column = "run_counter"),
            @Result(property = "executionStatus", column = "execution_status")
    })
    List<StatusJobExecutionDTO> getStatusJobExecution(@Param("jobName") String jobName,
                                                      @Param("quantity") Integer quantity);

    @Select("CALL SP_GET_JOB_EXECUTION_BY_NAME(" +
            "#{folder}," +
            "#{orderId}," +
            "#{jobName})")
    @Results({
            @Result(property = "jobName", column = "job_name"),
            @Result(property = "folder", column = "folder"),
            @Result(property = "application", column = "application"),
            @Result(property = "subApplication", column = "sub_application"),
            @Result(property = "jsonName", column = "json_name"),
            @Result(property = "orderId", column = "order_id"),
            @Result(property = "orderDate", column = "order_date"),
            @Result(property = "startTime", column = "start_time"),
            @Result(property = "endTime", column = "end_time"),
            @Result(property = "executionStatus", column = "execution_status"),
            @Result(property = "projectName", column = "project_name"),
            @Result(property = "scrumTeam", column = "scrum_team"),
            @Result(property = "scrumMaster", column = "scrum_master"),
            @Result(property = "solutionDetail", column = "solution_detail"),
            @Result(property = "errorType", column = "error_type"),
            @Result(property = "errorDesc", column = "error_desc"),
            @Result(property = "errorReason", column = "error_reason"),
            @Result(property = "errorReasonDesc", column = "error_reason_desc"),
            @Result(property = "updatedBy", column = "updated_by"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "createdBy", column = "created_by"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "sdatoolId", column = "sdatool_id"),
            @Result(property = "domain", column = "service_owner"),
            @Result(property = "isTypified", column = "isTypified"),
            @Result(property = "logArgos", column = "log_argos"),
            @Result(property = "runCounter", column = "run_counter"),
            @Result(property = "executionType", column = "execution_type"),
            @Result(property = "ticketJira", column = "ticket_jira"),
            @Result(property = "domainId", column = "domain_id"),
            @Result(property = "issueActionsId", column = "issue_actions_id"),
            @Result(property = "jobId", column = "job_id"),
            @Result(property = "folderName", column = "folder_name"),
            @Result(property = "devEmail", column = "dev_email"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "statusType", column = "status_type"),
            @Result(property = "commentActionsDesc", column = "comment_actions_desc"),
            @Result(property = "createAuditDate", column = "create_audit_date"),
            @Result(property = "createAuditUser", column = "create_audit_user"),
            @Result(property = "updateAuditDate", column = "update_audit_date"),
            @Result(property = "updateAuditUser", column = "update_audit_user")
    })
    JobExecutionByIdDTO getJobExecutionById(@Param("folder") String folder,
                                            @Param("orderId") String orderId,
                                            @Param("jobName") String jobName);
}