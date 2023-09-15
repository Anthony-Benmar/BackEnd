package com.bbva.dto.bui.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationDataResponse {
    private Integer id;
    private Integer sourceId;
    private String fuenteAnteriorId;
    private String sourceName;
    private Integer folioId;
    private String folioCode;
    private Integer projectId;
    private String sdatool;
    private String projectName;
    private Integer resolutionSourceTypeId;
    private String resolutionSourceType;
    private Integer ingestSourceTypeId;
    private String ingestSourceType;
    private Integer statusFolioTypeId;
    private String statusFolioType;
}
