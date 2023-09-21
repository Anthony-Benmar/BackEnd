package com.bbva.database.mappers;

import com.bbva.dto.batch.response.JobExecutionFilterData;
import com.bbva.dto.usecasetracking.GetUseTrackingJobsSPResponse;
import com.bbva.dto.usecasetracking.UseCaseDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UseCaseTrackingMapper {
    @Select({"SELECT * " +
            "FROM use_case " +
            "WHERE domain_id = #{domainId} AND subgroup_use_case is null;"})
    List<UseCaseDto> getUseCaseByDomainId(@Param("domainId") int domainId);

    @Select("CALL SP_GET_USE_CASE_TRACKING_JOBS(" +
            "#{pageCurrent}," +
            "#{recordsAmount}," +
            "#{useCaseId}," +
            "#{orderDate})")
    @Results({
            @Result(property = "use_case_id", column = "use_case_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "domain_id", column = "domain_id"),
            @Result(property = "subgroup_use_case", column = "subgroup_use_case"),
            @Result(property = "job_name", column = "job_name"),
            @Result(property = "execution_status", column = "execution_status"),
            @Result(property = "functional_description", column = "functional_description"),
            @Result(property = "additional_functional_description", column = "additional_functional_description"),
            @Result(property = "order_date", column = "order_date"),
            @Result(property = "start_time", column = "start_time"),
            @Result(property = "end_time", column = "end_time"),
            @Result(property = "view_type", column = "view_type"),
            @Result(property = "records_count", column = "records_count")
    })
    List<GetUseTrackingJobsSPResponse> getUseCaseTrackingJobs(
                                        @Param("pageCurrent") int page,
                                        @Param("recordsAmount") int recordsAmount,
                                        @Param("useCaseId") int useCaseId,
                                        @Param("orderDate") String orderDate);
}
