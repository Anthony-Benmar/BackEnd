package com.bbva.dto.buc.request;

import com.bbva.dto.PaginationMasterDtoRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationDtoRequest extends PaginationMasterDtoRequest {
    private String folio_code;
    private String codigo_campo;
    private String nombre_proyecto;
    private Number id_fuente;
    private Integer prioridad;
    private Integer estado_resolucion;
    private String descripcion_funcional;
}
