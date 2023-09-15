package com.bbva.database.mappers;

import com.bbva.entities.buc.BucPagedFilteredEntity;
import com.bbva.entities.buc.BucmapEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.ArrayList;

@Mapper
public interface BucMapper {

    @Select("CALL SP_BUC_PAGED_FILTERED(" +
            "#{page}," +
            "#{records_amount}," +
            "#{folio_code}," +
            "#{field_code}," +
            "#{project_name}," +
            "#{source_id}," +
            "#{priority}," +
            "#{resolucion_state}," +
            "#{functional_description})")
    ArrayList<BucPagedFilteredEntity> getPagination(@Param("page") int page,
                                              @Param("records_amount") int records_amount,
                                              @Param("folio_code") String folio_code,
                                              @Param("field_code") String field_code,
                                              @Param("project_name") String project_name,
                                              @Param("source_id") Number source_id,
                                              @Param("priority") Integer priority,
                                              @Param("resolucion_state") Integer resolucion_state,
                                              @Param("functional_description") String functional_description);

    @Select("CALL SP_BUC_FILTERED_BY_ID(" +
            "#{buc_id})")
    ArrayList<BucmapEntity> getReadOnly(@Param("buc_id") int buc_id);
}