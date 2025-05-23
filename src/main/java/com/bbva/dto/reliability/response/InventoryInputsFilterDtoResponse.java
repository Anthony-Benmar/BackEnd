package com.bbva.dto.reliability.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InventoryInputsFilterDtoResponse {
    private int count;
    private int pagesAmount;
    private List<InventoryInputsDtoResponse> data;
}
