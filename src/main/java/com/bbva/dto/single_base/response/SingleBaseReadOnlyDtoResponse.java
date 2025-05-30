package com.bbva.dto.single_base.response;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class SingleBaseReadOnlyDtoResponse {
    private Integer id;
    private String folio;
    private String projectName;
    private String ucSourceName;
    private String ucSourceDesc;

    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date registeredFolioDate;

    private String statusFolioType;
    private String analystProjectId;
    private String analystCaId;
    private String resolutionSourceType;

    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date resolutionSourceDate;

    private String reusedFolioCode;
    private String resolutionCommentDesc;
    private String folioType;
    private Double oldSourceId;
    private String ucFinalistDesc;
    private String catalogId;
}