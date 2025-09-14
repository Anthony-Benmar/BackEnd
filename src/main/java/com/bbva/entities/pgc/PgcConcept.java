package com.bbva.entities.pgc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "pgc_concepts")

public class PgcConcept {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "file_upload_id", nullable = false)
    private int fileUploadId;

    @Column(name = "row_index", nullable = false)
    private int rowIndex;

    @Column(name = "dominio")
    private String dominio;

    @Column(name = "sdatool")
    private String sdatool;

    @Column(name = "grupo_datos")
    private String grupoDatos;

    @Column(name = "dato_funcional")
    private String datoFuncional;

    @Column(name = "descripcion_dato_funcional", columnDefinition = "TEXT")
    private String descripcionDatoFuncional;

    @Column(name = "campo_origen_conocido")
    private String campoOrigenConocido;

    @Column(name = "fuente_origen_conocida")
    private String fuenteOrigenConocida;

    @Column(name = "descripcion_de_la_fuente_conocida", columnDefinition = "TEXT")
    private String descripcionDeLaFuenteConocida;

    @Column(name = "nombre_de_sistema_origen")
    private String nombreDeSistemaOrigen;

    @Column(name = "contacto_responsable")
    private String contactoResponsable;

    @Column(name = "periodicidad_requerida")
    private String periodicidadRequerida;

    @Column(name = "informacion_adicional", columnDefinition = "TEXT")
    private String informacionAdicional;

    @Column(name = "campo_mandatory")
    private String campoMandatory;

    @Column(name = "dato_relevante")
    private String datoRelevante;

    @Column(name = "is_valid")
    private boolean isValid;

    @Column(name = "is_included")
    private boolean isIncluded;

    @Column(name = "validation_comments", columnDefinition = "JSON")
    private String validationComments;

    @Column(name = "user_record")
    private String usuarioRegistro;

    @Column(name = "register_date")
    private Date fechaRegistro;

    @Column(name = "user_modification")
    private String usuarioModificacion;

    @Column(name = "modification_date")
    private Date fechaModificacion;

    @Column(name = "application")
    private String solicitud;

    @Column(name = "state")
    private String estado;

    public void setId(int id) {
        this.id = id;
    }
}
