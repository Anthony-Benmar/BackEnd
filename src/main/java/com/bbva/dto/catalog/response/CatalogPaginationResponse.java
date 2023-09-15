package com.bbva.dto.catalog.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CatalogPaginationResponse {
    private Integer id;
    private String processCode;
    private String name;
    private String description;
    private Integer time;
}
