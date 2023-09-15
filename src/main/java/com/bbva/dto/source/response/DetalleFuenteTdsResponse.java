package com.bbva.dto.source.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleFuenteTdsResponse {
    private String tipo_origen;
    private String comentarios_tds;
    private String periodicidad;
    private String detalla_periodicidad;
    private String tipo_descarga_datio;
    private String informacion_master;
    private String campos;
    private String uuaa_raw;
    private String nombre_origen;
}
