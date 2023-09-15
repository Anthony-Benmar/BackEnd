package com.bbva.dto.bui.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadOnlyIngestaResponse {
    private String persistencia_destino;
    private String dashboard;
    private String fecha_dashboard;
    private String comentario_general;
}
