package com.bbva.database.mappers;

import com.bbva.database.sql.UserSqlUtil;
import com.bbva.dto.User.Request.PaginationDtoRequest;
import com.bbva.entities.User;
import com.bbva.entities.secu.RolMenu;
import com.bbva.entities.secu.RolMenuAction;
import com.bbva.entities.secu.UserRole;
import org.apache.ibatis.annotations.*;

import java.util.List;


public interface UserMapper {


    String SQL_QUERY_USER_BY_EMPLOYEE_ID = "SELECT user_id, google_id, full_name, email, status_type, employee_id " +
            "FROM secu_user WHERE employee_id = #{employeeId}";

    String SQL_QUERY_USER_BY_USER_ID = "<script>" +
            "SELECT user_id, google_id, full_name, email, status_type, employee_id " +
            "FROM secu_user WHERE user_id IN "+
            "<foreach item='item' index='index' collection='arrayUserId' open='(' separator=',' close=')'> #{item} </foreach>" +
            "</script>";
    String SQL_QUERY_USERS = "SELECT user_id AS userId, employee_id AS employeeId, " +
                                "google_id AS googleId, email, " +
                                "full_name AS fullName, " +
                                "status_type AS statusType, " +
                                "operation_user AS operationUser, " +
                                "operation_date AS operationDate " +
                                "FROM secu_user";

    String SQL_QUERY_USERS_BY_EMAIL = "CALL SP_FILTER_USER (#{email})";

    String SQL_QUERY_INSERT_USER="INSERT INTO secu_user ( google_id, full_name, email, status_type, operation_user, operation_date, employee_id) " +
            "VALUES (#{googleId},#{fullName},#{email}, #{statusType}, #{operationUser}, now(), #{employeeId})";

    String SQL_QUERY_UPDATE_USER_EMPLOYEE_ID = "UPDATE secu_user SET employee_id = #{employeeId} WHERE user_id = #{userId}";


    String SQL_QUERY_INSERT_ROLES="INSERT INTO secu_user_role (user_id, role_id, status_type, operation_user, operation_date) " +
            "VALUES (#{userId}, #{roleId}, #{statusType}, #{operationUser}, now())";

    String SQL_QUERY_DELETE_ROLES = "DELETE FROM secu_user_role WHERE user_id = #{user_id}";

    String SQL_QUERY_DELETE_USER="DELETE FROM secu_user WHERE user_id = #{user_id};";

    String SQL_QUERY_ROLE_MENU_USER="SELECT sur.user_id, sur.role_id, sr.role_name, sr.role_desc , srm.menu_id, sm.menu_desc, sm.menu_icon, sm.menu_url, sm.menu_order, srm.role_menu_id, sm.menu_parent, sm2.menu_desc as menu_parent_desc, sm2.menu_icon as menu_icon_parent, sm2.menu_order as menu_order_parent " +
            "FROM secu_user_role sur INNER JOIN secu_role sr ON sur.role_id=sr.role_id INNER JOIN secu_role_menu srm ON sur.role_id = srm.role_id " +
            "INNER JOIN secu_menu sm ON srm.menu_id = sm.menu_id " +
            "LEFT JOIN secu_menu sm2  ON sm.menu_parent=sm2.menu_id " +
            "WHERE sur.user_id = #{user_id};";

    String SQL_QUERY_ROLE_MENU_ACTION = "select mar.menu_property AS menuPropiedad, mar.action_property AS accionPropiedad, (not isnull(srma.role_menu_action_id)) AS autorizado  from " +
                                                "( select sm.menu_property, sm.menu_id, sa.action_property, sa.action_id, sr.role_id  " +
                                                "from secu_menu sm, secu_action sa, secu_role sr " +
                                                "where not sm.menu_property is null ) mar " +
                                                "left join secu_role_menu srm on mar.role_id = srm.role_id and mar.menu_id = srm.menu_id " +
                                                "left join secu_role_menu_action srma on srm.role_menu_id = srma.role_menu_id and srma.action_id = mar.action_id " +
                                                "where mar.role_id = #{roleId}";

    @Select(SQL_QUERY_USERS)
    List<User> list();


    @Select(SQL_QUERY_USER_BY_EMPLOYEE_ID)
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "googleId", column = "google_id"),
            @Result(property = "fullName", column = "full_name"),
            @Result(property = "email", column = "email"),
            @Result(property = "statusType", column = "status_type"),
            @Result(property = "employeeId", column = "employee_id")
    })
    List<User> findByEmployeeID(@Param("employeeId") String employeeId);

    @Select(SQL_QUERY_USER_BY_USER_ID)
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "googleId", column = "google_id"),
            @Result(property = "fullName", column = "full_name"),
            @Result(property = "email", column = "email"),
            @Result(property = "statusType", column = "status_type"),
            @Result(property = "employeeId", column = "employee_id")
    })
    List<User> findByUserID(@Param("arrayUserId") int[] arrayUserId);

    @Select(SQL_QUERY_USERS_BY_EMAIL)
    List<User> listByEmail(String email);

    @Select(SQL_QUERY_ROLE_MENU_USER)
    List<RolMenu> roleMenuList(@Param("user_id") int user_id);

    @Insert(SQL_QUERY_INSERT_USER)
    @Options(useGeneratedKeys = true, keyProperty = "userId", keyColumn = "user_id")
    void insertUser(User user);

    @Insert(SQL_QUERY_INSERT_ROLES)
    @Options(useGeneratedKeys = true, keyProperty = "userRoleId", keyColumn = "user_role_id")
    void insertRoles(UserRole userRole);

    @Delete(SQL_QUERY_DELETE_USER)
    void deleteUser(int user_id);

    @Delete(SQL_QUERY_DELETE_ROLES)
    void deleteRoles(int user_id);

    @Update(SQL_QUERY_UPDATE_USER_EMPLOYEE_ID)
    void updateUserEmployeeId(User user);

    @Select(SQL_QUERY_ROLE_MENU_ACTION)
    List<RolMenuAction> listPermissions(int roleId);

    @SelectProvider(type = UserSqlUtil.class, method = "listarPaginado")
    List<User> listarPaginado(PaginationDtoRequest dto);

    @SelectProvider(type = UserSqlUtil.class, method = "contarTotalPaginado")
    int contarTotalPaginado(PaginationDtoRequest dto);

}