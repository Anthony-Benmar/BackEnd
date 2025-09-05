package com.bbva.entities.template;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TemplateTest {

    @Test
    void debe_setear_y_leer_fase_y_subFase() {
        Template t = new Template();
        t.setTemplate_id(1);
        t.setType_id(10);
        t.setLabel_one("DM1");
        t.setProcess_code("Mapeo");
        t.setName("Validación X");
        t.setDescription("desc");
        t.setOrden(1);
        t.setStatus(1);

        t.setFase("Data");
        t.setSubFase("Mapeo");

        assertEquals(1, t.getTemplate_id());
        assertEquals(10, t.getType_id());
        assertEquals("DM1", t.getLabel_one());
        assertEquals("Mapeo", t.getProcess_code());
        assertEquals("Validación X", t.getName());
        assertEquals("desc", t.getDescription());
        assertEquals(1, t.getOrden());
        assertEquals(1, t.getStatus());

        assertEquals("Data", t.getFase());
        assertEquals("Mapeo", t.getSubFase());

        assertNotNull(t.toString());
        assertNotEquals(0, t.hashCode());
    }
}
