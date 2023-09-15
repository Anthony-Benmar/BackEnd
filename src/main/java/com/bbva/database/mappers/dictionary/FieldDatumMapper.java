package com.bbva.database.mappers.dictionary;

import java.util.List;

import org.apache.ibatis.annotations.SelectProvider;

import com.bbva.database.mappers.dictionary.sql.FieldDatumSqlUtil;
import com.bbva.entities.dictionary.FieldDatumEntity;

public interface FieldDatumMapper {
    
    @SelectProvider(type = FieldDatumSqlUtil.class, method = "filtrar")
    List<FieldDatumEntity> filtrar(String physicalFieldName);

    @SelectProvider(type = FieldDatumSqlUtil.class, method = "buscarPorId")
    FieldDatumEntity buscarPorId(Integer fieldDatumId);

}
