package com.bbva.dto.board.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationDataBoardFuenteCampoDtoResponse {
    private Integer boardFieldId;
    private String fuenteId;
    private String fuenteNombre;
    private String boardCampoNombre;
}
