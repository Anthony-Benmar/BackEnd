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
    private String registeredFolioDate;  // Puede ser de tipo Date si prefieres manejar fechas de esa forma.
    private String statusFolioType;  // Enum, pero lo manejamos como String.
    private String analystProjectId;
    private String analystCaId;
    private String resolutionSourceType;
    private String resolutionSourceDate;  // Puede ser de tipo Date también.
    private String reusedFolioCode;
    private String resolutionCommentDesc;
    private String folioType;  // Enum, manejado como String.
    private BigDecimal oldSourceId;  // Usamos BigDecimal para manejar decimales.
    private String ucFinalistDesc;
    private String catalogId;

}