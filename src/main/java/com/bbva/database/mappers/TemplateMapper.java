package com.bbva.database.mappers;

import com.bbva.entities.template.Template;
import com.bbva.entities.template.TemplateEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

public interface TemplateMapper {

    @Select("SELECT template.template_id, " +
            "       template.name, " +
            "       template.label_one, " +
            "       template.orden, " +
            "       template.type_id, " +
            "       fase.element_name AS fase, " +
            "       template.process_code AS subFase " +
            "FROM jira_template template " +
            "LEFT JOIN catalog fase " +
            "  ON template.type_id = fase.element_id " +
            " AND fase.catalog_id = 1023 " +
            "WHERE template.status = 1 AND template.type_id = #{typeId} " +
            "ORDER BY template.orden ASC")
    List<Template> findActiveTemplatesByType(@Param("typeId") String typeId);

    @Select("SELECT template.label_one, fase.element_name fase, template.process_code sub_fase, template.name " +
            "FROM jira_template template " +
            "LEFT JOIN catalog fase " +
            "ON template.type_id = fase.element_id " +
            "AND fase.catalog_id = 1023 " +
            "WHERE template.status = 1")
    List<Template> list();

    @Select({"<script>" +
            "SELECT * FROM jira_template p " +
            "WHERE p.template_id IN " +
            "<foreach item='item' index='index' collection='list' open='(' separator=',' close=')'> #{item} </foreach>" +
            "</script>"})
    List<Template> listById(@Param("list") List<Integer> listId);

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
    ArrayList<TemplateEntity> pagination(@Param("page") int page,
                                         @Param("records_amount") int records_amount,
                                         @Param("folio_code") String folio_code,
                                         @Param("field_code") String field_code,
                                         @Param("project_name") String project_name,
                                         @Param("source_id") Number source_id,
                                         @Param("priority") Integer priority,
                                         @Param("resolucion_state") Integer resolucion_state,
                                         @Param("functional_description") String functional_description);
}
