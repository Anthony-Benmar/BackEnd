package com.bbva.dto.buc.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadOnlyDtoResponse {
    private ReadOnlyBucmapDtoResponse roadmap;
    private ReadOnlyResolucionDtoResponse resolucion;
}
