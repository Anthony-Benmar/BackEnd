package com.bbva.dto.template.request;

import com.bbva.dto.PaginationMasterDtoRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationDtoRequest extends PaginationMasterDtoRequest {
    private Integer type;
}
