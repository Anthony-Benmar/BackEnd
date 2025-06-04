package com.bbva.dto.source_with_parameter.request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceWithParameterPaginationDtoRequest {
    private Integer limit;
    private Integer offset;
    private String status;
    private String OriginType;
    private String TdsOpinionDebt;
    private String EffectivenessDebt;
}
