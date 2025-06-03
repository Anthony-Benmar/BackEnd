package com.bbva.dto.source_with_parameter.request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceWithParameterPaginationDtoRequest {
    private Integer limit;
    private Integer offset;
    private String TdsDescription;
    private String TdsSource;
    private String ReplacementId;
    private String OriginType;
    private String TdsOpinionDebt;
    private String EffectivenessDebt;
}
