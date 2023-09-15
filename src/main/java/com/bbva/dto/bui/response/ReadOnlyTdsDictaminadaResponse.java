package com.bbva.dto.bui.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadOnlyTdsDictaminadaResponse {
    private String id_tds;
    private String fuenteId;
    private String fuente_tds;
    private String historia_ingestar;
    private String comentario;
    private String estado_tds;
    private String id_reemplazado;
    private String estado_deuda;
    private String comentario_deuda;
}
