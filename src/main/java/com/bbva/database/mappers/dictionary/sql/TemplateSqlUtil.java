package com.bbva.database.mappers.dictionary.sql;

import org.apache.ibatis.jdbc.SQL;

public final class TemplateSqlUtil {
    
    public static final String TABLE_NAME = "dict_template";

    public String obtenerVigente(){
        return new SQL()
                .SELECT("template_id as templateId")
                .SELECT("type")
                .SELECT("version")
                .SELECT("current")
                .SELECT("file_name as fileName")
    			.FROM(TABLE_NAME)
    			.WHERE("type = #{type}")
                .WHERE("current = 1").toString();
    }

}
