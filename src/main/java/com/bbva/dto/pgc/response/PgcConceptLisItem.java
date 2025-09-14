package com.bbva.dto.pgc.response;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PgcConceptLisItem {
    private String dominio;
    private String sdatool;
    private String grupoDatos;
    private String datoFuncional;
    private String descripcionDatoFuncional;
    private String campoOrigenConocido;
    private String fuenteOrigenConocida;
    private String descripcionDeLaFuenteConocida;
    private String nombreDeSistemaOrigen;
    private String contactoResponsable;
    private String periodicidadRequerida;
    private String informacionAdicional;
    private String campoMandatory;
    private String datoRelevante;

    // Renombrados en @Results
    private String usuarioRegistro;
    private Date fechaRegistro;
    private String usuarioModificacion;
    private Date   fechaModificacion;
    private String solicitud;
    private String estado;
}
