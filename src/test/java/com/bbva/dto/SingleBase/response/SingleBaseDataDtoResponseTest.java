package com.bbva.dto.SingleBase.response;


import com.bbva.dto.single_base.response.SingleBaseDataDtoResponse;
import org.junit.jupiter.api.Test;

class SingleBaseDataDtoResponseTest {
    @Test
    void testGettersAndSetters(){
        SingleBaseDataDtoResponse dto = new SingleBaseDataDtoResponse();

        // Set values
        dto.setId(1);
        dto.setFolio("F123");
        dto.setProjectName("Project A");
        dto.setUcSourceName("UC Source");
        dto.setUcSourceDesc("UC Source Description");
        dto.setRegisteredFolioDate(new java.util.Date());
        dto.setStatusFolioType("Active");
        dto.setAnalystProjectId("AP123");
        dto.setAnalystCaId("CA123");
        dto.setResolutionSourceType("Type A");
        dto.setResolutionSourceDate(new java.util.Date());
        dto.setReusedFolioCode("RF123");
        dto.setResolutionCommentDesc("Resolution Comment");
        dto.setFolioType("Type B");
        dto.setOldSourceId(100.0);
        dto.setUcFinalistDesc("Finalist Description");
        dto.setCatalogId("CAT123");

        // Assert values
        assertBasicFields(dto);
    }
    private void assertBasicFields(SingleBaseDataDtoResponse dto){
        assert dto.getId() == 1;
        assert "F123".equals(dto.getFolio());
        assert "Project A".equals(dto.getProjectName());
        assert "UC Source".equals(dto.getUcSourceName());
        assert "UC Source Description".equals(dto.getUcSourceDesc());
        assert dto.getRegisteredFolioDate() != null;
        assert "Active".equals(dto.getStatusFolioType());
        assert "AP123".equals(dto.getAnalystProjectId());
        assert "CA123".equals(dto.getAnalystCaId());
        assert "Type A".equals(dto.getResolutionSourceType());
        assert dto.getResolutionSourceDate() != null;
        assert "RF123".equals(dto.getReusedFolioCode());
        assert "Resolution Comment".equals(dto.getResolutionCommentDesc());
        assert "Type B".equals(dto.getFolioType());
        assert dto.getOldSourceId() == 100.0;
        assert "Finalist Description".equals(dto.getUcFinalistDesc());
        assert "CAT123".equals(dto.getCatalogId());
    }
}
