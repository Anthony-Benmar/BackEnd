package com.bbva.dto.bui.request;

import com.bbva.dto.PaginationMasterDtoRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationDtoRequest extends PaginationMasterDtoRequest {
    private String sdatool;
    private String proposed_table;
    private String analyst_in_charge;
    private String folio_code;
    private Number id_fuente;
    private Integer tipo;
    private Integer estado;
}
