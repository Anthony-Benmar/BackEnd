package com.bbva.dto.User.Response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginationResponse {
    
    private Integer count;
    private Integer pages_amount;
    private List<PaginationDataResponse> data;
    
}
