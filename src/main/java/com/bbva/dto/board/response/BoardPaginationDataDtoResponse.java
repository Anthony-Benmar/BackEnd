package com.bbva.dto.board.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardPaginationDataDtoResponse {
    private Integer fuente;
    private Integer deuda_tecnica;
    private String fuente_antigua;
    private Integer proyecto;
    private String proyecto_sdatool;
    private String proyecto_nombre;
    private String fuente_origen;
    private Integer estado_fuente;
    private String estado_fuente_descripcion;
    private Integer estado_deuda;
    private String estado_deuda_descripcion;
    private Integer tipologia;
    private String tipologia_descripcion;
    private String tabla_master;
    private Integer estado_deuda_tecnica;
    private String estado_deuda_tecnica_descripcion;
    private String tabla_raw;
    private String complejidad_deuda;
}
