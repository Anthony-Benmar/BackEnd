package com.bbva.database.mappers.dictionary;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import com.bbva.database.mappers.dictionary.sql.TemplateSqlUtil;
import com.bbva.entities.dictionary.TemplateEntity;

public interface TemplateMapper {
    
    @SelectProvider(type = TemplateSqlUtil.class, method = "obtenerVigente")
    TemplateEntity obtenerVigente(@Param("type") String type);
    
}
