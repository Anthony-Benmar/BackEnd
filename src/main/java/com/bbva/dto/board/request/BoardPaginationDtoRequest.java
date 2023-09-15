package com.bbva.dto.board.request;

import com.bbva.dto.PaginationMasterDtoRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardPaginationDtoRequest extends PaginationMasterDtoRequest {
    private Number id_fuente;
    private String fuente_origen;
    private String fuente_datio;
    private String funcional_descripcion;
}
