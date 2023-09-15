package com.bbva.dto.source.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationDataDtoResponse {
    private Float id;
    private Integer sourceId;
    private String fuente;
    private String descripcion;
    private Integer estado_id;
    private String estado;
    private Integer tipo_origen_id;
    private String tipo_origen;
    private Integer uuaa_master_id;
    private String uuaa_master;
    private Integer deuda_id;
    private String deuda;
}
