package com.bbva.database.mappers;

import com.bbva.entities.secu.Menu;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MenuMapper {
    
    String SQL_QUERY_MENU = "SELECT menu_id AS menuId, menu_desc AS menuDesc FROM secu_menu WHERE NOT menu_url IS NULL AND menu_url <> ''";

    @Select(SQL_QUERY_MENU)
    List<Menu> list();

}
