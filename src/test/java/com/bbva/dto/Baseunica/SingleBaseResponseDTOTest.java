package com.bbva.dto.Baseunica;
import com.bbva.dto.singleBase.response.SingleBaseResponseDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SingleBaseResponseDTOTest {

    @Test
    void testGettersAndSetters() {
        SingleBaseResponseDTO dto = new SingleBaseResponseDTO();

        // Establecer valores en el DTO
        dto.setFolio("FOLIO123");
        dto.setProjectName("ProjectA");
        dto.setUcSourceName("SourceNameA");
        dto.setUcSourceDesc("Description of source");
        dto.setRegisteredFolioDate("2023-01-01");
        dto.setStatusFolioType("Finalizada");
        dto.setAnalystProjectId("Project123");
        dto.setAnalystCaId("CA123");
        dto.setResolutionSourceType("TypeA");
        dto.setResolutionSourceDate("2023-01-02");
        dto.setReusedFolioCode("ReusedFolio123");
        dto.setResolutionCommentDesc("Resolution comments");
        dto.setFolioType("Entrada");
        dto.setOldSourceId(new BigDecimal("123.4"));  // Usando BigDecimal en lugar de Double
        dto.setUcFinalistDesc("Finalist Description");
        dto.setCatalogId("Catalog123");

        // Verificar los valores establecidos a través de los getters
        assertEquals("FOLIO123", dto.getFolio());
        assertEquals("ProjectA", dto.getProjectName());
        assertEquals("SourceNameA", dto.getUcSourceName());
        assertEquals("Description of source", dto.getUcSourceDesc());
        assertEquals("2023-01-01", dto.getRegisteredFolioDate());
        assertEquals("Finalizada", dto.getStatusFolioType());
        assertEquals("Project123", dto.getAnalystProjectId());
        assertEquals("CA123", dto.getAnalystCaId());
        assertEquals("TypeA", dto.getResolutionSourceType());
        assertEquals("2023-01-02", dto.getResolutionSourceDate());
        assertEquals("ReusedFolio123", dto.getReusedFolioCode());
        assertEquals("Resolution comments", dto.getResolutionCommentDesc());
        assertEquals("Entrada", dto.getFolioType());
        assertEquals(new BigDecimal("123.4"), dto.getOldSourceId());  // Verificar con BigDecimal
        assertEquals("Finalist Description", dto.getUcFinalistDesc());
        assertEquals("Catalog123", dto.getCatalogId());
    }
}
