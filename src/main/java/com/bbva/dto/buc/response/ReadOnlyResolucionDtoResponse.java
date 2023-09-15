package com.bbva.dto.buc.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ReadOnlyResolucionDtoResponse {
    private String estado_resolucion;
    private String resolucion;
    private String comentario_resolucion;
    private String logica;
    private List<ReadOnlyResolucionFieldsDtoResponse> fields;
}
