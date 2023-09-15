package com.bbva.database.mappers;

import com.bbva.entities.coreassurance.SourceEntity;
import com.bbva.entities.source.SourcePagedFilteredEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SourceMapper {
    @Select("CALL SP_SOURCE_PAGED_FILTERED(" +
            "#{page}," +
            "#{records_amount}," +
            "#{source_id}," +
            "#{source_name}," +
            "#{raw_func_map_id}," +
            "#{estado_id}," +
            "#{origen}," +
            "#{debtstatus_source_type}," +
            "#{uuaa_master}," +
            "#{tds_desc}," +
            "#{tabla_master}," +
            "#{propietario_global})")
    List<SourcePagedFilteredEntity> pagination(@Param("page") Integer page,
                                               @Param("records_amount") Integer records_amount,
                                               @Param("source_id") Number source_id,
                                               @Param("source_name") String source_name,
                                               @Param("raw_func_map_id") String raw_func_map_id,
                                               @Param("estado_id") Integer estado_id,
                                               @Param("origen") Integer origen,
                                               @Param("debtstatus_source_type") Integer debtstatus_source_type,
                                               @Param("uuaa_master") String uuaa_master,
                                               @Param("tds_desc") String tds_desc,
                                               @Param("tabla_master") String tabla_master,
                                               @Param("propietario_global") Integer propietario_global);

    @Select("CALL SP_SOURCE_FILTERED_BY_ID(#{source_id})")
    SourceEntity getReadOnly(@Param("source_id") Integer source_id);
}