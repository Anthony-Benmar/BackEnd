package com.bbva.dto.board.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BoardPaginationDtoResponse {
    private Integer count;
    private Integer pages_amount;
    private List<BoardPaginationDataDtoResponse> data;
}
