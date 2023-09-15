package com.bbva.database.mappers.dictionary.sql;

import org.apache.ibatis.jdbc.SQL;

public final class FieldDatumSqlUtil {
    
    public static final String TABLE_NAME = "dict_field_datum";

    public String filtrar(){
        return new SQL()
                .SELECT("field_datum_id as fieldDatumId")
                .SELECT("physical_field_name as physicalFieldName")
                .SELECT("logical_field_name as logicalFieldName")
                .SELECT("description_field_desc as descriptionFieldDesc")
    			.FROM(TABLE_NAME)
    			.WHERE("physical_field_name LIKE CONCAT('%',#{physicalFieldName},'%')").toString();
    }

    public String buscarPorId(){
        return new SQL()
                .SELECT("field_datum_id as fieldDatumId")
                .SELECT("physical_field_name as physicalFieldName")
                .SELECT("logical_field_name as logicalFieldName")
                .SELECT("description_field_desc as descriptionFieldDesc")
    			.FROM(TABLE_NAME)
    			.WHERE("field_datum_id = #{fieldDatumId}").toString();
    }

}
