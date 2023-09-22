package com.bbva.dto.government.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterSourceResponseDTO {
    private int count;
    private int pages_amount;
    private List<SourceDefinitionDTOResponse> data;
}
