package com.bbva.dto.catalog.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ListByCatalogIdDtoResponse {
    private ArrayList<ListByCatalogIdGroupByCatalogDtoResponse> catalog;
}
