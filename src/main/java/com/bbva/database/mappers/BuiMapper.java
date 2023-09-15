package com.bbva.database.mappers;

import com.bbva.entities.bui.BuiPagedFilteredEntity;
import com.bbva.entities.bui.BuiEntity;
import com.bbva.entities.coreassurance.SourceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface BuiMapper {
    @Select("CALL SP_BUI_PAGED_FILTERED(" +
            "#{pageCurrent}," +
            "#{recordsAmount}," +
            "#{sdatool}," +
            "#{proposed_table}," +
            "#{analyst_in_charge}," +
            "#{folio_code},"+
            "#{id_fuente}," +
            "#{tipo}," +
            "#{estado})")
    List<BuiPagedFilteredEntity> pagination(@Param("pageCurrent") Integer page,
                                            @Param("recordsAmount") Integer records_amount,
                                            @Param("sdatool") String sdatool,
                                            @Param("proposed_table") String proposed_table,
                                            @Param("analyst_in_charge") String analyst_in_charge,
                                            @Param("folio_code") String folio_code,
                                            @Param("id_fuente") Number id_fuente,
                                            @Param("tipo") Integer tipo,
                                            @Param("estado") Integer estado);

    @Select("CALL SP_BUI_FILTERED_BY_ID(#{bui_id})")
    List<BuiEntity> getReadOnly(@Param("bui_id") Integer bui_id);

}