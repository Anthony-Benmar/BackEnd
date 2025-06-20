package com.bbva.dto.reliability.response;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PaginationReliabilityPackResponseTest {
    @Test
    void testGettersAndSetters() {
        PaginationReliabilityPackResponse response = new PaginationReliabilityPackResponse();

        // Setup
        response.setCount(2);
        response.setPagesAmount(1);

        ReliabilityPacksDtoResponse pack1 = new ReliabilityPacksDtoResponse();
        pack1.setPack("PACK001");

        ReliabilityPacksDtoResponse pack2 = new ReliabilityPacksDtoResponse();
        pack2.setPack("PACK002");

        List<ReliabilityPacksDtoResponse> dataList = Arrays.asList(pack1, pack2);
        response.setData(dataList);

        // Assertions
        assertEquals(2, response.getCount());
        assertEquals(1, response.getPagesAmount());
        assertEquals(2, response.getData().size());
        assertEquals("PACK001", response.getData().get(0).getPack());
        assertEquals("PACK002", response.getData().get(1).getPack());
    }
}
