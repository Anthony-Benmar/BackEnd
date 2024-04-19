package com.bbva.database.mappers;

import com.bbva.dto.job.response.JobBasicInfoDtoResponse;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface JobBasicInfoMapper {
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
}
