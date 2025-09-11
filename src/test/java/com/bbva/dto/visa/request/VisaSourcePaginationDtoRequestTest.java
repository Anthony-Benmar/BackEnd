package com.bbva.dto.visa.request;

import org.junit.jupiter.api.Test;

import com.bbva.dto.visa_sources.request.VisaSourcePaginationDtoRequest;

import static org.junit.jupiter.api.Assertions.*;

class VisaSourcePaginationDtoRequestTest {

    @Test
    void testGettersAndSetters() {
        VisaSourcePaginationDtoRequest dto = new VisaSourcePaginationDtoRequest();

        dto.setLimit(10);
        dto.setOffset(5);
        dto.setId(123);
        dto.setQuarter("2025-Q3");
        dto.setRegisterDate("2025-09-10");
        dto.setDomain("Finance");
        dto.setUserStory("US-001");

        assertBasicFields(dto);
    }

     private void assertBasicFields(VisaSourcePaginationDtoRequest dto) {
         assertEquals(10, dto.getLimit());
         assertEquals(5, dto.getOffset());
         assertEquals(123, dto.getId());
         assertEquals("2025-Q3", dto.getQuarter());
         assertEquals("2025-09-10", dto.getRegisterDate());
         assertEquals("Finance", dto.getDomain());
         assertEquals("US-001", dto.getUserStory());
     }
}