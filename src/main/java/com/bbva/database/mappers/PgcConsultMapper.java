package com.bbva.database.mappers;

import com.bbva.dto.pgc.response.PgcDocumentLisItem;
import com.bbva.dto.pgc.response.PgcConceptLisItem;
import com.bbva.entities.pgc.PgcConcept;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

public interface PgcConsultMapper {

    // CONSULT-PGC
    @Select("CALL sidedb.SP_GET_DOCUMENTS()")
    @Options(statementType = StatementType.CALLABLE)
    @Results({
            @Result(property = "id",               column = "id"),
            @Result(property = "domainName",       column = "domain_name"),
            @Result(property = "sdatool",          column = "sdatool"),
            @Result(property = "projectName",      column = "project_name"),
            @Result(property = "uploadedAt",       column = "uploaded_at",       jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "modificationDate", column = "modification_date", jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "qRegistro",        column = "q_registro")
    })
    List<PgcDocumentLisItem> getProcessedDocumentsForList();

    // CONSULT-PGC
    @Select("CALL sidedb.SP_GET_CONCEPTS(#{documentId})")
    @Options(statementType = StatementType.CALLABLE)
    @Results({
            @Result(property = "dominio",                        column = "dominio"),
            @Result(property = "sdatool",                        column = "sdatool"),
            @Result(property = "grupoDatos",                     column = "grupo_datos"),
            @Result(property = "datoFuncional",                  column = "dato_funcional"),
            @Result(property = "descripcionDatoFuncional",       column = "descripcion_dato_funcional"),
            @Result(property = "campoOrigenConocido",            column = "campo_origen_conocido"),
            @Result(property = "fuenteOrigenConocida",           column = "fuente_origen_conocida"),
            @Result(property = "descripcionDeLaFuenteConocida",  column = "descripcion_de_la_fuente_conocida"),
            @Result(property = "nombreDeSistemaOrigen",          column = "nombre_de_sistema_origen"),
            @Result(property = "contactoResponsable",            column = "contacto_responsable"),
            @Result(property = "periodicidadRequerida",          column = "periodicidad_requerida"),
            @Result(property = "informacionAdicional",           column = "informacion_adicional"),
            @Result(property = "campoMandatory",                 column = "campo_mandatory"),
            @Result(property = "datoRelevante",                  column = "dato_relevante"),
            @Result(property = "usuarioRegistro",                column = "user_record"),
            @Result(property = "fechaRegistro",                  column = "register_date",     jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "usuarioModificacion",            column = "user_modification"),
            @Result(property = "fechaModificacion",              column = "modification_date", jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "solicitud",                      column = "application"),
            @Result(property = "estado",                         column = "state")
    })
    List<PgcConceptLisItem> getConceptsByDocument(@Param("documentId") Integer documentId);

    @Update("""
    UPDATE pgc_concepts
    SET
        dominio = #{dominio},
        sdatool = #{sdatool},
        grupo_datos = #{grupoDatos},
        dato_funcional = #{datoFuncional},
        descripcion_dato_funcional = #{descripcionDatoFuncional},
        campo_origen_conocido = #{campoOrigenConocido},
        fuente_origen_conocida = #{fuenteOrigenConocida},
        descripcion_de_la_fuente_conocida = #{descripcionDeLaFuenteConocida},
        nombre_de_sistema_origen = #{nombreDeSistemaOrigen},
        contacto_responsable = #{contactoResponsable},
        periodicidad_requerida = #{periodicidadRequerida},
        informacion_adicional = #{informacionAdicional},
        campo_mandatory = #{campoMandatory},
        dato_relevante = #{datoRelevante},
        user_record = #{userRecord},
        register_date = #{registerDate},
        application = #{application},
        state = #{state}
    WHERE id = #{id}
""")
    void updatePgcConcept(PgcConcept concept);
}
