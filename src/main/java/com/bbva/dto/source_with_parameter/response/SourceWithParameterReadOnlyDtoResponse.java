package com.bbva.dto.source_with_parameter.response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceWithParameterReadOnlyDtoResponse {
    private Integer id;
    private String tdsDescription;
    private String tdsSource;
    private String sourceOrigin;
    private String originType;
    private String status;
    private String replacementId;
    private String modelOwner;
    private String masterRegisteredBoard;
    private String dataLakeLayer;
    private String uuaaRaw;
    private String uuaaMaster;
    private String tdsOpinionDebt;
    private String debtLevel;
    private String inheritedSourceId;
    private String opinionDebtComments;
    private String missingCertification;
    private String missingFieldProfiling;
    private String incompleteOpinion;
    private String pdcoProcessingUse;
    private String effectivenessDebt;
    private String ingestionType;
    private String ingestionLayer;
    private String datioDownloadType;
    private String processingInputTableIds;
    private String periodicity;
    private String periodicityDetail;
    private String folderUrl;
    private String typology;
    private String criticalTable;
    private String criticalTableOwner;
    private String l1t;
    private String hem;
    private String his;
    private String err;
    private String log;
    private String mlg;
    private String quality;
    private String tag1;
    private String tag2;
    private String tag3;
    private String tag4;
    private String rawPath;
}
