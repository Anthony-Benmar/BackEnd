package com.bbva.entities.issueticket;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrder2Test {

    @Test
    void testAllArgsConstructor() {
        int workOrderId = 100;
        String feature = "Nueva Feature";
        String folio = "FOLIO-2025";
        int boardId = 10;
        int projectId = 20;
        String sourceId = "SRC-01";
        String sourceName = "Source Name";
        int flowType = 2;
        int workOrderType = 1;
        int statusType = 5;
        String registerUserId = "user01";
        Date registerDate = new Date();
        Date endDate = new Date();
        Integer recordsCount = 44;

        WorkOrder2 workOrder = new WorkOrder2(
                workOrderId, feature, folio, boardId, projectId, sourceId,
                sourceName, flowType, workOrderType, statusType, registerUserId,
                registerDate, endDate, recordsCount
        );

        assertNotNull(workOrder);
        assertEquals(workOrderId, workOrder.getWork_order_id());
        assertEquals(feature, workOrder.getFeature());
        assertEquals(folio, workOrder.getFolio());
        assertEquals(boardId, workOrder.getBoard_id());
        assertEquals(projectId, workOrder.getProject_id());
        assertEquals(sourceId, workOrder.getSource_id());
        assertEquals(sourceName, workOrder.getSource_name());
        assertEquals(flowType, workOrder.getFlow_type());
        assertEquals(workOrderType, workOrder.getWork_order_type());
        assertEquals(statusType, workOrder.getStatus_type());
        assertEquals(registerUserId, workOrder.getRegister_user_id());
        assertEquals(registerDate, workOrder.getRegister_date());
        assertEquals(endDate, workOrder.getEnd_date());
        assertEquals(recordsCount, workOrder.getRecords_count());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        WorkOrder2 workOrder = new WorkOrder2();

        workOrder.setWork_order_id(200);
        workOrder.setFeature("Feature Setter");
        workOrder.setFolio("FOLIO-SETTER");
        workOrder.setBoard_id(30);
        workOrder.setProject_id(40);
        workOrder.setSource_id("SRC-SET");
        workOrder.setSource_name("Source Setter");
        workOrder.setFlow_type(3);
        workOrder.setWork_order_type(2);
        workOrder.setStatus_type(10);
        workOrder.setRegister_user_id("user02");
        Date now = new Date();
        workOrder.setRegister_date(now);
        workOrder.setEnd_date(null);
        workOrder.setRecords_count(null);

        assertNotNull(workOrder);
        assertEquals(200, workOrder.getWork_order_id());
        assertEquals("Feature Setter", workOrder.getFeature());
        assertEquals("FOLIO-SETTER", workOrder.getFolio());
        assertEquals(30, workOrder.getBoard_id());
        assertEquals(40, workOrder.getProject_id());
        assertEquals("SRC-SET", workOrder.getSource_id());
        assertEquals("Source Setter", workOrder.getSource_name());
        assertEquals(3, workOrder.getFlow_type());
        assertEquals(2, workOrder.getWork_order_type());
        assertEquals(10, workOrder.getStatus_type());
        assertEquals("user02", workOrder.getRegister_user_id());
        assertEquals(now, workOrder.getRegister_date());
        assertNull(workOrder.getEnd_date());
        assertNull(workOrder.getRecords_count());
    }
}