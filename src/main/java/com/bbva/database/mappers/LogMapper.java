package com.bbva.database.mappers;

import com.bbva.entities.secu.LogEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface LogMapper {
    String SQL_QUERY_LOGS = "SELECT * FROM secu_log";

    String SQL_QUERY_INSERT_LOG="INSERT INTO secu_log (log_id, process_id, period, process_name, message, status_type, operation_user, operation_date) " +
            "VALUES (#{logId}, #{processId},#{period},#{processName},#{message}, #{statusType}, #{operationUser}, #{operationDate})";

    @Select(SQL_QUERY_LOGS)
    List<LogEntity> list();

    @Insert(SQL_QUERY_INSERT_LOG)
    @Options(useGeneratedKeys = false, keyProperty = "logId")
    void insertLog(LogEntity log);
}
