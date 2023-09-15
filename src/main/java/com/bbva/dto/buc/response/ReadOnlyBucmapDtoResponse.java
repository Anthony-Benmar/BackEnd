package com.bbva.dto.buc.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadOnlyBucmapDtoResponse {
    private String folio;
    private String sdatool;
    private String prioridad;
    private String codigo_campo;
    private String dato_funcional;
    private String descripcion_funcional;
}
