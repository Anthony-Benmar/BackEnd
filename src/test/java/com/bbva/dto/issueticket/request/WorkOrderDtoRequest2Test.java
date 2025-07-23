package com.bbva.dto.issueticket.request;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WorkOrderDtoRequest2Test {

    @Test
    void shouldSetAndGetAllFields() {
        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();

        dto.setWorkOrderId(1);
        dto.setFeature("Nueva Feature");
        dto.setJiraProjectId(123);
        dto.setJiraProjectName("PROJ123");
        dto.setFolio("FOLIO-2025");
        dto.setBoardId(10);
        dto.setProjectId(20);
        dto.setSourceId("SRC-01");
        dto.setSourceName("Source Name");
        dto.setFlowType(2);
        dto.setFaseId("F1");
        dto.setSprintEst("Sprint 10");
        dto.setRegisterUserId("user01");
        dto.setUsername("brigittemendez");
        dto.setToken("token123");
        dto.setExpireTokenDate(123456789L);

        WorkOrderDetailDtoRequest detail = new WorkOrderDetailDtoRequest();
        dto.setWorkOrderDetail(Collections.singletonList(detail));

        dto.setLabels(Arrays.asList("label1", "label2"));
        dto.setE2e("SI");
        dto.setPeriod(Arrays.asList("2024-01", "2024-02"));

        assertEquals(1, dto.getWorkOrderId());
        assertEquals("Nueva Feature", dto.getFeature());
        assertEquals(123, dto.getJiraProjectId());
        assertEquals("PROJ123", dto.getJiraProjectName());
        assertEquals("FOLIO-2025", dto.getFolio());
        assertEquals(10, dto.getBoardId());
        assertEquals(20, dto.getProjectId());
        assertEquals("SRC-01", dto.getSourceId());
        assertEquals("Source Name", dto.getSourceName());
        assertEquals(2, dto.getFlowType());
        assertEquals("F1", dto.getFaseId());
        assertEquals("Sprint 10", dto.getSprintEst());
        assertEquals("user01", dto.getRegisterUserId());
        assertEquals("brigittemendez", dto.getUsername());
        assertEquals("token123", dto.getToken());
        assertEquals(123456789L, dto.getExpireTokenDate());
        assertEquals(1, dto.getWorkOrderDetail().size());
        assertEquals("label1", dto.getLabels().get(0));
        assertEquals("label2", dto.getLabels().get(1));
        assertEquals("SI", dto.getE2e());
        assertEquals("2024-01", dto.getPeriod().get(0));
        assertEquals("2024-02", dto.getPeriod().get(1));
    }

    @Test
    void shouldHandleEmptyAndNullLists() {
        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();

        dto.setWorkOrderDetail(null);
        dto.setLabels(null);
        dto.setPeriod(null);

        assertNull(dto.getWorkOrderDetail());
        assertNull(dto.getLabels());
        assertNull(dto.getPeriod());

        dto.setWorkOrderDetail(Collections.emptyList());
        dto.setLabels(Collections.emptyList());
        dto.setPeriod(Collections.emptyList());

        assertEquals(0, dto.getWorkOrderDetail().size());
        assertEquals(0, dto.getLabels().size());
        assertEquals(0, dto.getPeriod().size());
    }

    @Test
    void shouldAllowModifyingLists() {
        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();

        dto.setLabels(Arrays.asList("etiqueta1"));
        assertEquals(1, dto.getLabels().size());
        assertEquals("etiqueta1", dto.getLabels().get(0));

        dto.getLabels().set(0, "etiqueta2");
        assertEquals("etiqueta2", dto.getLabels().get(0));
    }
}