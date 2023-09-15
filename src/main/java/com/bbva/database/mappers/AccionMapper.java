package com.bbva.database.mappers;

import com.bbva.entities.secu.Accion;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AccionMapper {
    
    String SQL_QUERY_ACTION = "SELECT action_id AS accionId, action_name AS accionDesc FROM secu_action";

    @Select(SQL_QUERY_ACTION)
    List<Accion> list();

}
