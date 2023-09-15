package com.bbva.dto.source.response;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;

@Getter
@Setter
public class ReadOnlyResolucionDtoResponse {
    private String estado_resolucion;
    private String resolucion;
    private String comentario_resolucion;
    private String logica;
    private ArrayList<ReadOnlyResolucionFieldsDtoResponse> fields;
}
