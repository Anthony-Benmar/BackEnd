package com.bbva.dto.single_base.response;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class SingleBaseResponseDTO {
    private Integer id;
    private String folio;
    private String projectName;
    private String ucSourceName;
    private String ucSourceDesc;
    private String registeredFolioDate;
    private String statusFolioType;
    private String analystProjectId;
    private String analystCaId;
    private String resolutionSourceType;
    private String resolutionSourceDate;
    private String reusedFolioCode;
    private String resolutionCommentDesc;
    private String folioType;
    private BigDecimal oldSourceId;
    private String ucFinalistDesc;
    private String catalogId;
}