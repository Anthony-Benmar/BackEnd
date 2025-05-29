package com.bbva.dto.single_base.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class SingleBasePaginatedResponseDTO {
    private List<SingleBaseResponseDTO> data;
    private int totalCount;
}