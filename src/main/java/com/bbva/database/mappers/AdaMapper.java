package com.bbva.database.mappers;

import com.bbva.dto.ada.request.AdaJobExecutionFilterRequestDTO;
import com.bbva.dto.ada.response.AdaJobExecutionFilterData;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AdaMapper {

    @Select("CALL SP_FILTER_JOB_EXECUTION_ADA(" +
            "#{request.page}," +
            "#{request.records_amount}," +
            "#{request.jobName}," +
            "#{request.startDate}," +
            "#{request.endDate}," +
            "#{request.frequency}," +
            "#{request.isTransferred}," +
            "#{request.jobType}," +
            "#{request.serverExecution}," +
            "#{request.domain})")
    @Result(property = "jobName", column = "job_name")
    @Result(property = "parentFolder", column = "parent_folder")
    @Result(property = "orderDate", column = "order_date")
    @Result(property = "orderIdADA", column = "order_id_ada")
    @Result(property = "orderIdCCR", column = "order_id_ccr")
    @Result(property = "statusADA", column = "status_ada")
    @Result(property = "statusCCR", column = "status_ccr")
    @Result(property = "serverExecution", column = "server_execution")
    @Result(property = "serviceOwner", column = "service_owner")
    @Result(property = "isTransferred", column = "is_transferred")
    @Result(property = "jobType", column = "job_type")
    @Result(property = "frequency", column = "frequency")
    @Result(property = "recordsCount", column = "records_count")
    List<AdaJobExecutionFilterData> filter(@Param("request") AdaJobExecutionFilterRequestDTO request);
}