package com.bbva.database.mappers;

import com.bbva.entities.mesh.JobExecution;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MeshMapper {

    @Select("CALL SP_SELECT_JOB_EXECUTIONS_LATER()")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "job_id", column = "job_id"),
            @Result(property = "father_job_id", column = "father_job_id"),
            @Result(property = "lookup_job_name", column = "lookup_job_name"),
            @Result(property = "job_name", column = "job_name"),
            @Result(property = "json_name", column = "json_name"),
            @Result(property = "folder", column = "folder"),
            @Result(property = "application", column = "application"),
            @Result(property = "sub_application", column = "sub_application")
    })
    List<JobExecution> ListJobExecutionsLaters();

    @Select("CALL SP_SELECT_JOBS_EXECUTION_PREVIOUS()")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "job_id", column = "job_id"),
            @Result(property = "father_job_id", column = "father_job_id"),
            @Result(property = "lookup_job_name", column = "lookup_job_name"),
            @Result(property = "job_name", column = "job_name"),
            @Result(property = "json_name", column = "json_name"),
            @Result(property = "folder", column = "folder"),
            @Result(property = "application", column = "application")
    })
    List<JobExecution> ListJobExecutionsPrevious();

    @Select("CALL SP_JOB_EXECUTION_BY_ODATE(#{orderDate}, #{jobName})")
    @Results({
            @Result(property = "job_id", column = "job_id"),
            @Result(property = "job_name", column = "job_name"),
            @Result(property = "order_date", column = "order_date"),
            @Result(property = "start_time", column = "start_time"),
            @Result(property = "host", column = "host"),
            @Result(property = "run_as", column = "run_as"),
            @Result(property = "status", column = "execution_status")
    })
    List<JobExecution> ListStatusJobExecutions(
            @Param("orderDate") String orderDate,
            @Param("jobName")   String jobName
    );
}
