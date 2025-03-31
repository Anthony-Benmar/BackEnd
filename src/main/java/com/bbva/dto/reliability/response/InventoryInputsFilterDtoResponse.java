package com.bbva.dto.reliability.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InventoryInputsFilterDtoResponse {
    public int count;
    public int pages_amount;
    public List<InventoryInputsDtoResponse> data;
}
