package com.bbva.dto.source.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadOnlyDtoResponse {
    private InformacionGeneralResponse informacionGeneral;
    private DetalleFuenteTdsResponse detalleFuenteTds;
    private MapaFuncionalResponse mapaFuncional;
    private DeudaDictamenResponse deudaDictamen;
}
