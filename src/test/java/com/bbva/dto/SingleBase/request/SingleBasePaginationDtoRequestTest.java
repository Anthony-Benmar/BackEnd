package com.bbva.dto.SingleBase.request;

import com.bbva.dto.single_base.request.SingleBasePaginationDtoRequest;
import org.junit.jupiter.api.Test;

class SingleBasePaginationDtoRequestTest {
     @Test
    void testGettersAndSetters() {
        SingleBasePaginationDtoRequest dto = new SingleBasePaginationDtoRequest();

        // Set values
        dto.setLimit(10);
        dto.setOffset(0);
        dto.setId(1);
        dto.setProjectName("Project A");
        dto.setTipoFolio("Type A");
        dto.setFolio("F123");
        dto.setRegisteredFolioDate("2023-10-01");
        dto.setOldSourceId("100");

        // Assert values
        assertBasicFields(dto);
    }
    private void assertBasicFields(SingleBasePaginationDtoRequest dto) {
        assert dto.getLimit() == 10;
        assert dto.getOffset() == 0;
        assert dto.getId() == 1;
        assert "Project A".equals(dto.getProjectName());
        assert "Type A".equals(dto.getTipoFolio());
        assert "F123".equals(dto.getFolio());
        assert "2023-10-01".equals(dto.getRegisteredFolioDate());
        assert "100".equals(dto.getOldSourceId());
    }
}
