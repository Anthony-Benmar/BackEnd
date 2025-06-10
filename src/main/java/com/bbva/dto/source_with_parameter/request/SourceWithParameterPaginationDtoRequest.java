package com.bbva.dto.source_with_parameter.request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceWithParameterPaginationDtoRequest {
    private Integer limit;
    private Integer offset;
    private String id;
    private String tdsSource;
    private String uuaaMaster;
    private String modelOwner;
    private String status;
    private String originType;
    private String tdsOpinionDebt;
    private String effectivenessDebt;
}
