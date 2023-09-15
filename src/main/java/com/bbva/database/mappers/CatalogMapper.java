package com.bbva.database.mappers;

import com.bbva.entities.common.CatalogEntity;
import com.bbva.entities.common.PeriodEntity;
import com.bbva.entities.common.ProjectEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

public interface CatalogMapper {
    @Select({"<script>" +
            "SELECT c.catalog_id,c.element_id,c.element_name,c.status_type FROM catalog c " +
            "WHERE c.status_type = 1 AND c.catalog_id IN " +
            "<foreach item='item' index='index' collection='list' open='(' separator=',' close=')'> #{item} </foreach>" +
            "</script>"})
    ArrayList<CatalogEntity> getListByCatalog(@Param("list") int[] listId);

    @Select({"<script>" +
            "SELECT period_id,period_order,period_status FROM data_period " +
            "ORDER BY period_order DESC" +
            "</script>"})
    List<PeriodEntity> listAllPeriods();
}
