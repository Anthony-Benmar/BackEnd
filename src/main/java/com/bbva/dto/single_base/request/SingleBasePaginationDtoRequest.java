package com.bbva.dto.single_base.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleBasePaginationDtoRequest {
    private Integer limit;
    private Integer offset;
    private String projectName;
    private String tipoFolio;
    private String folio;
}