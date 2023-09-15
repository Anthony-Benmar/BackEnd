package com.bbva.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationMasterDtoRequest {
    private Integer records_amount;
    private Integer page;
    private Integer offset;
}
