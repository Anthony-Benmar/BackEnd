package com.bbva.dto.visa.response;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.Test;

import com.bbva.dto.visa_sources.response.VisaSourcesDataDtoResponse;
import com.bbva.dto.visa_sources.response.VisaSourcesPaginationDtoResponse;

class VisaSourcesPaginationDtoResponseTest {
    @Test
    void testGettersAndSetters() {
        VisaSourcesPaginationDtoResponse dto = new VisaSourcesPaginationDtoResponse();

        VisaSourcesDataDtoResponse dataItem = new VisaSourcesDataDtoResponse();
        dataItem.setId(1);
        dataItem.setSourceType("Database");

        List<VisaSourcesDataDtoResponse> dataList = Collections.singletonList(dataItem);

        dto.setTotalCount(5);
        dto.setData(dataList);

        assertEquals(5, dto.getTotalCount());
        assertNotNull(dto.getData());
        assertEquals(1, dto.getData().size());
        assertEquals(1, dto.getData().get(0).getId());
        assertEquals("Database", dto.getData().get(0).getSourceType());
    }   
}