package com.bbva.database.mappers;

import com.bbva.entities.mesh.JobExecution;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MeshMapper {

    @Select("select round(rand() * 1000000000) id,jes.* from job_finder_side_fch jes")
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

    @Select("select round(rand() * 1000000000) id,jes.* from job_finder_side_chf jes")
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

    @Select("select job_id,job_name,order_date,start_time,host,run_as,execution_status " +
            "from job_execution je WHERE je.order_date = #{orderDate}")
    @Results({
            @Result(property = "job_id", column = "job_id"),
            @Result(property = "job_name", column = "job_name"),
            @Result(property = "order_date", column = "order_date"),
            @Result(property = "start_time", column = "start_time"),
            @Result(property = "host", column = "host"),
            @Result(property = "run_as", column = "run_as"),
            @Result(property = "status", column = "execution_status")
    })
    List<JobExecution> ListStatusJobExecutions(@Param("orderDate") String orderDate);



}
