package com.bbva.database.mappers;

import com.bbva.dto.batch.request.InsertReliabilityIncidenceDTO;
import com.bbva.dto.batch.response.JobExecutionFilterData;
import com.bbva.entities.InsertEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

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
            "#{domain})")
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
            @Result(property = "solutionType", column = "solution_type"),
            @Result(property = "solutionDesc", column = "solution_desc"),
            @Result(property = "solutionDetail", column = "solution_detail"),
            @Result(property = "jobType", column = "job_type"),
            @Result(property = "jobDesc", column = "job_desc"),
            @Result(property = "errorType", column = "error_type"),
            @Result(property = "errorDesc", column = "error_desc"),
            @Result(property = "errorDetails", column = "error_details"),
            @Result(property = "updatedBy", column = "updated_by"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "createdBy", column = "created_by"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "sdatoolId", column = "sdatool_id"),
            @Result(property = "domain", column = "service_owner"),
            @Result(property = "recordsCount", column = "records_count"),
            @Result(property = "isTypified",column = "isTypified")
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
                                        @Param("domain") String domain);

    @Select("CALL SP_INSERT_RELIABILITY_INCIDENCE(" +
            "#{jobName}," +
            "#{orderDate}," +
            "#{orderId}," +
            "#{jobType}," +
            "#{errorType}," +
            "#{errorDetails}," +
            "#{solutionType}," +
            "#{solutionDetail}," +
            "#{employeeId})")
    @Results({
            @Result(property = "last_insert_id", column = "last_insert_id"),
            @Result(property = "new_register", column = "new_register")
    })
    InsertEntity insertReliabilityIncidence(InsertReliabilityIncidenceDTO dto);

}
