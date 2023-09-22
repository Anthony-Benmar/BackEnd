package com.bbva.database.mappers.dictionary.sql;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import com.bbva.dto.dictionary.parameter.GenerationSearchParameter;
import com.bbva.enums.dictionary.StatusGenerationType;

public final class GenerationSqlUtil {
    
    public static final String TABLE_NAME = "dict_generation";
    public static final String TABLE_NAME_PROJECT = "data_project DP";
    public static final int DAYS_INACTIVATE = 7;

    public String insertar() {
        return new SQL()
                .INSERT_INTO(TABLE_NAME)
                .VALUES("dictum_physical_file_name", "#{dictumPhysicalFileName}")
                .VALUES("generation_date", "#{generationDate}")
                .VALUES("status", "#{status}")
                .VALUES("dictum_template_id", "#{dictumTemplateId}")
                .VALUES("employee_id", "#{employeeId}")
                .VALUES("dictum_logical_file_name", "#{dictumLogicalFileName}")
                .VALUES("project_id", "#{projectId}")
                .VALUES("source_id", "#{sourceId}")
                .VALUES("source_name", "#{sourceName}")
				.toString();
    }
    
    public String finalizar() {
        return new SQL()
                .UPDATE(TABLE_NAME)
                .SET("status = #{status}")
                .SET("dictionary_physical_file_name = #{dictionaryPhysicalFileName}")
                .SET("dictionary_logical_file_name = #{dictionaryLogicalFileName}")
                .SET("dictionary_template_id = #{dictionaryTemplateId}")
                .SET("generation_complete_date = #{generationCompleteDate}")
                .SET("dictionary_file = #{dictionaryFile}")
                .WHERE("generation_id = #{generationId}")
				.toString();
    }

    public String buscarPorId() {
    	return new SQL()
                .SELECT("generation_id as generationId")
                .SELECT("dictum_physical_file_name as dictumPhysicalFileName")
                .SELECT("generation_date as generationDate")
                .SELECT("generation_complete_date as generationCompleteDate")
                .SELECT("status")
                .SELECT("dictum_template_id as dictumTemplateId")
                .SELECT("dictionary_template_id as dictionaryTemplateId")
                .SELECT("employee_id as employeeId")
                .SELECT("dictum_logical_file_name as dictumLogicalFileName")
                .SELECT("dictionary_physical_file_name as dictionaryPhysicalFileName")
                .SELECT("dictionary_logical_file_name as dictionaryLogicalFileName")
                .SELECT("project_id as projectId")
                .SELECT("source_id as sourceId")
                .SELECT("source_name as sourceName")
                .SELECT("dictionary_file as dictionaryFile")
    			.FROM(TABLE_NAME)
    			.WHERE("generation_id = #{generationId}").toString();
    }

    public String buscar(GenerationSearchParameter parameter) {
    	SQL sql = new SQL().SELECT("DG.generation_id as generationId")
                            .SELECT("DG.dictum_physical_file_name as dictumPhysicalFileName")
                            .SELECT("DG.generation_date as generationDate")
                            .SELECT("DG.generation_complete_date as generationCompleteDate")
                            .SELECT("DG.status")
                            .SELECT("DG.dictum_template_id as dictumTemplateId")
                            .SELECT("DG.dictionary_template_id as dictionaryTemplateId")
                            .SELECT("DG.employee_id as employeeId")
                            .SELECT("DG.dictum_logical_file_name as dictumLogicalFileName")
                            .SELECT("DG.dictionary_physical_file_name as dictionaryPhysicalFileName")
                            .SELECT("DG.dictionary_logical_file_name as dictionaryLogicalFileName")
                            .SELECT("DG.project_id as projectId")
                            .SELECT("DG.source_id as sourceId")
                            .SELECT("DG.source_name as sourceName")
                            .SELECT("DP.project_name as projectName")
                            .FROM(TABLE_NAME + " DG")
                            .INNER_JOIN(TABLE_NAME_PROJECT + " ON DG.project_id = DP.project_id");
                            
        if(parameter.getProjectId() != null && parameter.getProjectId() > 0){
            sql.WHERE("DG.project_id = #{projectId}");
        }
        if(StringUtils.isNotEmpty(parameter.getSourceId())){
            sql.WHERE("DG.source_id LIKE CONCAT('%',#{sourceId},'%')");
        }
        if(StringUtils.isNotEmpty(parameter.getSourceName())){
            sql.WHERE("DG.source_name LIKE CONCAT('%',#{sourceName},'%')");
        }
        if(parameter.getStartDate() != null){
            sql.WHERE("DATE(DG.generation_date) >= #{startDate}");
        }
        if(parameter.getEndingDate()!= null){
            sql.WHERE("DATE(DG.generation_date) <= #{endingDate}");
        }
        if(StringUtils.isNotEmpty(parameter.getEmployeeId())){
            sql.WHERE("DG.employee_id = #{employeeId}");
        }
        sql.WHERE(String.format("DG.status <> '%s'", StatusGenerationType.INACTIVO.getCodigo()));
        sql.ORDER_BY("DG.generation_date DESC");
        return sql.toString();
    }

    public String desactivarAntiguos(){
        return new SQL()
                .UPDATE(TABLE_NAME)
                .SET(String.format("status = '%s'", StatusGenerationType.INACTIVO.getCodigo()))
                .SET("dictionary_file = null")
                .SET("inactive_date = #{fechaCorte}")
                .WHERE(String.format("DATEDIFF(#{fechaCorte}, generation_date) > %s", DAYS_INACTIVATE))
                .WHERE(String.format("status <> '%s'", StatusGenerationType.INACTIVO.getCodigo()))
                .toString();
    }

}
