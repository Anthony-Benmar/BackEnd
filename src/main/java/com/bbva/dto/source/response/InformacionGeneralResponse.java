package com.bbva.dto.source.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class InformacionGeneralResponse {
    private String id;
    private String ruta_master;
    private String descripcion_tds;
    private String fuente_tds;
    private List reemplazo;
    private String estado_tds;
    private String dictamen_url;
    private String uuaa_master;
    private String estado_deuda;
    private String tipologia;
    private String owner_global_model;
}
