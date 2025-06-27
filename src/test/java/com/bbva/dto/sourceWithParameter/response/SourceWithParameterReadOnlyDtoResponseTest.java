package com.bbva.dto.sourceWithParameter.response;

import com.bbva.dto.source_with_parameter.response.SourceWithParameterReadOnlyDtoResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SourceWithParameterReadOnlyDtoResponseTest {
     @Test
    void testGettersAndSetters() {
        SourceWithParameterReadOnlyDtoResponse dto = new SourceWithParameterReadOnlyDtoResponse();

        // Set values
        dto.setId("1");
        dto.setTdsDescription("Test Description");
        dto.setTdsSource("Test Source");
        dto.setSourceOrigin("Test Origin");
        dto.setOriginType("Test Type");
        dto.setStatus("Active");
        dto.setReplacementId("R123");
        dto.setModelOwner("Owner");
        dto.setMasterRegisteredBoard("Board");
        dto.setDataLakeLayer("Layer");
        dto.setUuaaRaw("Raw");
        dto.setUuaaMaster("Master");
        dto.setTdsOpinionDebt("Debt");
        dto.setDebtLevel("High");
        dto.setInheritedSourceId("Inherited123");
        dto.setOpinionDebtComments("Comments");
        dto.setMissingCertification("Certification");
        dto.setMissingFieldProfiling("Profiling");
        dto.setIncompleteOpinion("Incomplete");
        dto.setPdcoProcessingUse("Use");
        dto.setEffectivenessDebt("Effective");
        dto.setIngestionType("Type");
        dto.setIngestionLayer("Layer");
        dto.setDatioDownloadType("Download");
        dto.setProcessingInputTableIds("Table123");
        dto.setPeriodicity("Monthly");
        dto.setPeriodicityDetail("Detail");
        dto.setFolderUrl("http://example.com");
        dto.setTypology("Typology");
        dto.setCriticalTable("Critical");
        dto.setCriticalTableOwner("Owner");
        dto.setL1t("L1T");
        dto.setHem("HEM");
        dto.setHis("HIS");
        dto.setErr("ERR");
        dto.setLog("LOG");
        dto.setMlg("MLG");
        dto.setQuality("High");
        dto.setTag1("Tag1");
        dto.setTag2("Tag2");
        dto.setTag3("Tag3");
        dto.setTag4("Tag4");
        dto.setRawPath("/path/to/raw");

         // Assert values in groups
         assertBasicFields(dto);
         assertTagsAndPaths(dto);
         assertCriticalFields(dto);
         assertDebs(dto);
    }
    private void assertBasicFields(SourceWithParameterReadOnlyDtoResponse dto) {
        assertEquals("1", dto.getId());
        assertEquals("Test Description", dto.getTdsDescription());
        assertEquals("Test Source", dto.getTdsSource());
        assertEquals("Test Origin", dto.getSourceOrigin());
        assertEquals("Test Type", dto.getOriginType());
        assertEquals("Active", dto.getStatus());
        assertEquals("R123", dto.getReplacementId());
        assertEquals("Owner", dto.getModelOwner());
        assertEquals("Board", dto.getMasterRegisteredBoard());
        assertEquals("Layer", dto.getDataLakeLayer());
    }
    private  void assertTagsAndPaths(SourceWithParameterReadOnlyDtoResponse dto) {
        assertEquals("Tag1", dto.getTag1());
        assertEquals("Tag2", dto.getTag2());
        assertEquals("Tag3", dto.getTag3());
        assertEquals("Tag4", dto.getTag4());
        assertEquals("/path/to/raw", dto.getRawPath());
    }
    private void assertCriticalFields(SourceWithParameterReadOnlyDtoResponse dto){
        assertEquals("Critical", dto.getCriticalTable());
        assertEquals("Owner", dto.getCriticalTableOwner());
        assertEquals("L1T", dto.getL1t());
        assertEquals("HEM", dto.getHem());
        assertEquals("HIS", dto.getHis());
        assertEquals("ERR", dto.getErr());
        assertEquals("LOG", dto.getLog());
        assertEquals("MLG", dto.getMlg());
        assertEquals("High", dto.getQuality());
    }
    private void assertDebs(SourceWithParameterReadOnlyDtoResponse dto) {
        assertEquals("Raw", dto.getUuaaRaw());
        assertEquals("Master", dto.getUuaaMaster());
        assertEquals("Debt", dto.getTdsOpinionDebt());
        assertEquals("High", dto.getDebtLevel());
        assertEquals("Inherited123", dto.getInheritedSourceId());
        assertEquals("Comments", dto.getOpinionDebtComments());
        assertEquals("Certification", dto.getMissingCertification());
        assertEquals("Profiling", dto.getMissingFieldProfiling());
        assertEquals("Incomplete", dto.getIncompleteOpinion());
        assertEquals("Use", dto.getPdcoProcessingUse());
        assertEquals("Effective", dto.getEffectivenessDebt());
        assertEquals("Type", dto.getIngestionType());
        assertEquals("Layer", dto.getIngestionLayer());
        assertEquals("Download", dto.getDatioDownloadType());
        assertEquals("Table123", dto.getProcessingInputTableIds());
        assertEquals("Monthly", dto.getPeriodicity());
        assertEquals("Detail", dto.getPeriodicityDetail());
        assertEquals("http://example.com", dto.getFolderUrl());
        assertEquals("Typology", dto.getTypology());
    }
}
