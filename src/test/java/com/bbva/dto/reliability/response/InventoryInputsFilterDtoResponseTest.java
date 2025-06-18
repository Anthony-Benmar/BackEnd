package com.bbva.dto.reliability.response;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryInputsFilterDtoResponseTest {

    @Test
    void testInventoryInputsFilterDtoResponse() {
        InventoryInputsDtoResponse item = new InventoryInputsDtoResponse();
        item.setJobName("Test Job");

        List<InventoryInputsDtoResponse> dataList = new ArrayList<>();
        dataList.add(item);

        InventoryInputsFilterDtoResponse response = new InventoryInputsFilterDtoResponse();
        response.setCount(10);
        response.setPagesAmount(2);
        response.setData(dataList);

        assertEquals(10, response.getCount());
        assertEquals(2, response.getPagesAmount());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals("Test Job", response.getData().get(0).getJobName());
    }
}
