package com.bbva.dto.source.request;

import com.bbva.dto.PaginationMasterDtoRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationDtoRequest extends PaginationMasterDtoRequest {
    private Number id_fuente;
    private String nombre_fuente;
    private String uuaa_aplicativo;
    private Integer estado;
    private Integer origen;
    private Integer estado_deuda;
    private String uuaa_master;
    private String descripcion_tds;
    private String tabla_master;
    private Integer propietario_global;
}
