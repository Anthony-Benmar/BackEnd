package com.bbva.database.mappers;

import com.bbva.entities.secu.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface RolMapper {
    
    String SQL_QUERY_ROLES = "SELECT role_id AS idRole, " +
                                "role_name AS nombre, " +
                                "role_desc AS descripcion " +
                                "FROM secu_role";

    String SQL_QUERY_MENU_IDS = "SELECT M.menu_id AS menuId FROM secu_menu M " +
                                "INNER JOIN secu_role_menu RM ON RM.menu_id = M.menu_id " +
                                "WHERE RM.role_id = #{role_id} AND NOT M.menu_url IS NULL AND M.menu_url <> ''; ";
    
    String SQL_QUERY_ACCION_IDS = "SELECT RM.menu_id AS menuId, RMA.action_id AS accionId FROM secu_role_menu_action RMA " +
                                    "INNER JOIN secu_role_menu RM ON RMA.role_menu_id = RM.role_menu_id " +
                                    "INNER JOIN secu_menu M ON RM.menu_id = M.menu_id " +
                                    "WHERE  RM.role_id = #{role_id} AND NOT M.menu_url IS NULL AND M.menu_url <> ''; ";

    String SQL_QUERY_INSERT_MENUS ="INSERT INTO secu_role_menu (role_id, menu_id) " +
            "VALUES (#{roleId}, #{menuId})";

    String SQL_QUERY_INSERT_ACCION ="INSERT INTO secu_role_menu_action (role_menu_id, action_id) " +
            "VALUES (#{rolMenuId}, #{actionId})";

    String SQL_QUERY_INSERT ="INSERT INTO secu_role (role_name, role_desc, operation_user, operation_date) " +
            "VALUES (#{nombre}, #{descripcion}, #{operationUser}, now())";

    String SQL_QUERY_UPDATE = "UPDATE secu_role SET role_name = #{nombre}, role_desc = #{descripcion} WHERE role_id = #{idRole}";

    String SQL_QUERY_DELETE_ACCION = "DELETE FROM secu_role_menu_action WHERE role_menu_id IN (SELECT role_menu_id FROM secu_role_menu WHERE role_id = #{idRole})";

    String SQL_QUERY_DELETE_MENU = "DELETE FROM secu_role_menu WHERE role_id = #{idRole}";

    @Select(SQL_QUERY_ROLES)
    List<Rol> list();

    @Select(SQL_QUERY_MENU_IDS)
    List<Menu> listIdsMenu(int role_id);

    @Select(SQL_QUERY_ACCION_IDS)
    List<Accion> listIdsMenuAccion(int role_id);

    @Insert(SQL_QUERY_INSERT_MENUS)
    @Options(useGeneratedKeys = true, keyProperty = "roleMenuId", keyColumn = "role_menu_id")
    void insertMenu(RolMenu rolMenu);

    @Insert(SQL_QUERY_INSERT_ACCION)
    @Options(useGeneratedKeys = true, keyProperty = "rolMenuActionId", keyColumn = "role_menu_action_id")
    void insertAccion(RolMenuAction rolMenuAction);

    @Insert(SQL_QUERY_INSERT)
    @Options(useGeneratedKeys = true, keyProperty = "idRole", keyColumn = "role_id")
    void insertRol(Rol rol);

    @Update(SQL_QUERY_UPDATE)
    void updateRol(Rol rol);

    @Delete(SQL_QUERY_DELETE_ACCION)
    void deleteAcciones(int idRole);

    @Delete(SQL_QUERY_DELETE_MENU)
    void deleteMenus(int idRole);

}
