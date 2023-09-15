package com.bbva.database.mappers.dictionary.sql;

import org.apache.ibatis.jdbc.SQL;

public final class GenerationFieldSqlUtil {
    
    public static final String TABLE_NAME = "dict_generation_field";

    public String insertar() {
        return new SQL()
                .INSERT_INTO(TABLE_NAME)
                .VALUES("field_name", "#{fieldName}")
                .VALUES("status", "#{status}")
                .VALUES("generation_id", "#{generationId}")
                .VALUES("physical_field_name", "#{physicalFieldName}")
                .VALUES("logical_field_name", "#{logicalFieldName}")
                .VALUES("description_field_desc", "#{descriptionFieldDesc}")
                .toString();
    }

    public String buscar(){
        return new SQL()
                .SELECT("generation_field_id as generationFieldId")
                .SELECT("field_name as fieldName")
                .SELECT("status")
                .SELECT("generation_id as generationId")
                .SELECT("physical_field_name as physicalFieldName")
                .SELECT("logical_field_name as logicalFieldName")
                .SELECT("description_field_desc as descriptionFieldDesc")
                .FROM(TABLE_NAME)
                .WHERE("generation_id = #{generationId}")
                .toString();            
    }

    public String buscarPorId(){
        return new SQL()
                .SELECT("generation_field_id as generationFieldId")
                .SELECT("field_name as fieldName")
                .SELECT("status")
                .SELECT("generation_id as generationId")
                .SELECT("physical_field_name as physicalFieldName")
                .SELECT("logical_field_name as logicalFieldName")
                .SELECT("description_field_desc as descriptionFieldDesc")
                .FROM(TABLE_NAME)
                .WHERE("generation_field_id = #{generationFieldId}")
                .toString();            
    }

    public String actualizarFieldDatum(){
        return new SQL()
                .UPDATE(TABLE_NAME)
                .SET("physical_field_name = #{physicalFieldName}")
                .SET("logical_field_name = #{logicalFieldName}")
                .SET("description_field_desc = #{descriptionFieldDesc}")
                .SET("status = #{status}")
                .WHERE("generation_field_id = #{generationFieldId}")
                .toString();
    }

}
