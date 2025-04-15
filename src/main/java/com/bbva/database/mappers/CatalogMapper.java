package com.bbva.database.mappers;

import com.bbva.entities.batch.GetCatalogEntity;
import com.bbva.entities.common.CatalogEntity;
import com.bbva.entities.common.PeriodEntity;
import com.bbva.entities.common.ProjectEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
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

    @Select({"<script>" +
            "CALL SP_GET_ACTIVE_PERIOD() " +
            "</script>"})
    List<PeriodEntity> getActivePeriod();

    @Select("CALL SP_LIST_CATALOG(" +
            "#{catalogId}," +
            "#{pageCurrent}," +
            "#{parentElementId})"
    )
    @Results({
            @Result(property = "catalogId", column = "catalog_id"),
            @Result(property = "elementId", column = "element_id"),
            @Result(property = "elementName", column = "element_name"),
            @Result(property = "elementDescription", column = "element_desc"),
            @Result(property = "statusType", column = "status_type"),
            @Result(property = "parentCatalogId", column = "parent_catalog_id"),
            @Result(property = "parentElementId", column = "parent_element_id")
    })
    ArrayList<GetCatalogEntity> getCatalog(
            @Param("catalogId") Integer catalogId,
            @Param("pageCurrent") Integer parentCatalogId,
            @Param("parentElementId") Integer parentElementId
    );
}