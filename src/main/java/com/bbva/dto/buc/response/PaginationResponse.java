package com.bbva.dto.buc.response;

import com.bbva.entities.buc.BoardPagedFilteredEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PaginationResponse {
    private Integer count;
    private Integer pages_amount;
    private List<PaginationDataResponse> data;

}
