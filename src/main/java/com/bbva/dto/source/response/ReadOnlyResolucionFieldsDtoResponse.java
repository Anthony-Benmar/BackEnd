package com.bbva.dto.source.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadOnlyResolucionFieldsDtoResponse {
    private String id;
    private String id_fuente;
    private String fuente_dictaminada;
    private String campo_dictaminado;
}
