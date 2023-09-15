package com.bbva.dto.bui.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadOnlyDictamenResponse {
    private String estatus;
    private String analista_proyecto;
    private String analista_ca;
    private String resolucion;
    private String folio_reuso;
    private String fecha_sprint_cierre;
    private String comentario_resolucion;
    private String tipo;
}
