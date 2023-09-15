package com.bbva.dto.source.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeudaDictamenResponse {
    private String estado_fuente;
    private String comentarios_deuda;
    private String nivel_deuda;
}
