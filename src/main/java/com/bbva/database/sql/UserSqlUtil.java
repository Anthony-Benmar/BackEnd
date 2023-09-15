package com.bbva.database.sql;

import com.bbva.dto.User.Request.PaginationDtoRequest;

public class UserSqlUtil {
    
    public String listarPaginado(PaginationDtoRequest dto){

        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("SELECT ");
        sbQuery.append("    U.user_id AS userId, ");
        sbQuery.append("    U.employee_id AS employeeId, ");
        sbQuery.append("    U.full_name AS fullName, ");
        sbQuery.append("    U.email, ");
        sbQuery.append("    U.operation_date AS operationDate, ");
        sbQuery.append("    GROUP_CONCAT(R.role_name SEPARATOR ', ') AS rolesNombre, ");
        sbQuery.append("    GROUP_CONCAT(UR.role_id SEPARATOR ', ') AS rolesId ");
        sbQuery.append("FROM secu_user U ");
        if(dto.getRol() > 0){
            sbQuery.append("INNER JOIN secu_user_role URC ON U.user_id = URC.user_id AND URC.role_id = #{rol} ");
        }        
        sbQuery.append("INNER JOIN secu_user_role UR ON U.user_id = UR.user_id ");
        sbQuery.append("INNER JOIN secu_role R ON UR.role_id = R.role_id ");
        sbQuery.append("WHERE (#{registro} IS NULL OR #{registro} = '' OR UPPER(U.employee_id) LIKE CONCAT('%', #{registro}, '%')) ");
        sbQuery.append("AND (#{nombreCompleto} IS NULL OR #{nombreCompleto} = '' OR UPPER(U.full_name) LIKE CONCAT('%', #{nombreCompleto}, '%')) ");
        sbQuery.append("AND (#{email} IS NULL OR #{email} = '' OR UPPER(U.email) LIKE CONCAT('%', #{email}, '%')) ");
        sbQuery.append("GROUP BY U.user_id, U.employee_id, U.full_name, U.email, U.operation_date ");
        sbQuery.append("ORDER BY U.user_id ");
        sbQuery.append("LIMIT #{records_amount} ");
        sbQuery.append("OFFSET #{offset} ");
       
        return sbQuery.toString();
    }

    public String contarTotalPaginado(PaginationDtoRequest dto){
        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("SELECT COUNT(*) ");
        sbQuery.append("FROM secu_user U ");
        if(dto.getRol() > 0){
            sbQuery.append("INNER JOIN secu_user_role URC ON U.user_id = URC.user_id AND URC.role_id = #{rol} ");
        }        
        sbQuery.append("WHERE (#{registro} IS NULL OR #{registro} = '' OR UPPER(U.employee_id) LIKE CONCAT('%', #{registro}, '%')) ");
        sbQuery.append("AND (#{nombreCompleto} IS NULL OR #{nombreCompleto} = '' OR UPPER(U.full_name) LIKE CONCAT('%', #{nombreCompleto}, '%')) ");
        sbQuery.append("AND (#{email} IS NULL OR #{email} = '' OR UPPER(U.email) LIKE CONCAT('%', #{email}, '%')) ");
       
        return sbQuery.toString();
    }

    

}
