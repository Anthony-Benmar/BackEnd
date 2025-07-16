package com.bbva.dto.reliability.request;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InventoryInputsFilterDtoRequestTest {

    @Test
    void testInventoryInputsFilterDtoRequest() {
        InventoryInputsFilterDtoRequest request = new InventoryInputsFilterDtoRequest();

        request.setPage(1);
        request.setRecordsAmount(10);
        request.setDomainName("Analytics");
        request.setUseCase("Fraud Detection");
        request.setJobType("Batch");
        request.setFrequency("Daily");
        request.setIsCritical("Yes");
        request.setSearchByInputOutputTable("input_table_xyz");

        assertEquals(1, request.getPage());
        assertEquals(10, request.getRecordsAmount());
        assertEquals("Analytics", request.getDomainName());
        assertEquals("Fraud Detection", request.getUseCase());
        assertEquals("Batch", request.getJobType());
        assertEquals("Daily", request.getFrequency());
        assertEquals("Yes", request.getIsCritical());
        assertEquals("input_table_xyz", request.getSearchByInputOutputTable());
        assertEquals("Oficina Central", request.getOrigin());
    }
}
