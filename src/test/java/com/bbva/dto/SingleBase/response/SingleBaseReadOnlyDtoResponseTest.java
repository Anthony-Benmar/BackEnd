package com.bbva.dto.SingleBase.response;

import com.bbva.dto.single_base.response.SingleBaseReadOnlyDtoResponse;
import org.junit.jupiter.api.Test;

 class SingleBaseReadOnlyDtoResponseTest {
    @Test
    void testGettersAndSetters() {
        SingleBaseReadOnlyDtoResponse dto = new SingleBaseReadOnlyDtoResponse();

        // Set values
        dto.setId("1");
        dto.setFolio("FOLIO123");
        dto.setProjectName("Project A");
        dto.setUcSourceName("Source Name");
        dto.setUcSourceDesc("Source Description");
        dto.setRegisteredFolioDate(new java.util.Date());
        dto.setStatusFolioType("Active");
        dto.setAnalystProjectId("Analyst123");
        dto.setAnalystCaId("CA123");
        dto.setResolutionSourceType("Type A");
        dto.setResolutionSourceDate(new java.util.Date());
        dto.setReusedFolioCode("Reused123");
        dto.setResolutionCommentDesc("Resolution Comment");
        dto.setFolioType("Type B");
        dto.setOldSourceId(2.0);
        dto.setUcFinalistDesc("Finalist Description");
        dto.setCatalogId("Catalog123");
        assertBasicFields(dto);
    }
    private void assertBasicFields(SingleBaseReadOnlyDtoResponse dto) {
        assert "1".equals(dto.getId());
        assert "FOLIO123".equals(dto.getFolio());
        assert "Project A".equals(dto.getProjectName());
        assert "Source Name".equals(dto.getUcSourceName());
        assert "Source Description".equals(dto.getUcSourceDesc());
        assert dto.getRegisteredFolioDate() != null;
        assert "Active".equals(dto.getStatusFolioType());
        assert "Analyst123".equals(dto.getAnalystProjectId());
        assert "CA123".equals(dto.getAnalystCaId());
        assert "Type A".equals(dto.getResolutionSourceType());
        assert dto.getResolutionSourceDate() != null;
        assert "Reused123".equals(dto.getReusedFolioCode());
        assert "Resolution Comment".equals(dto.getResolutionCommentDesc());
        assert "Type B".equals(dto.getFolioType());
        assert dto.getOldSourceId() == 2.0;
        assert "Finalist Description".equals(dto.getUcFinalistDesc());
        assert "Catalog123".equals(dto.getCatalogId());
    }
}
