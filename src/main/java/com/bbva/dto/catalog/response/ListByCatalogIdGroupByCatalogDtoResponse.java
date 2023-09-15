package com.bbva.dto.catalog.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ListByCatalogIdGroupByCatalogDtoResponse {
    private int catalogId;
    private String catalogDescription;
    private ArrayList<ListByCatalogIdGroupByCatalogGroupByElementDtoResponse> element;
}
