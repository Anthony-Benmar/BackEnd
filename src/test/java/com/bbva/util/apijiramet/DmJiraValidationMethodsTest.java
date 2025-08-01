package com.bbva.util.apijiramet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DmJiraValidationMethodsTest {

    private JsonObject buildSubtask(String summary,
                                    String issuetype,
                                    String priority,
                                    String status,
                                    String backlogValue,
                                    String description,
                                    String[] labels) {
        JsonObject subtask = new JsonObject();
        JsonObject fields = new JsonObject();
        fields.addProperty("summary", summary);

        JsonObject typeObj = new JsonObject();
        typeObj.addProperty("name", issuetype);
        fields.add("issuetype", typeObj);

        JsonObject prioObj = new JsonObject();
        prioObj.addProperty("name", priority);
        fields.add("priority", prioObj);

        JsonObject statusObj = new JsonObject();
        statusObj.addProperty("name", status);
        fields.add("status", statusObj);

        // custom backlog field
        JsonArray backlogArray = new JsonArray();
        backlogArray.add(backlogValue);
        fields.add("customfield_13300", backlogArray);

        fields.addProperty("description", description);

        // labels array
        JsonArray labelArray = new JsonArray();
        for (String lbl : labels) labelArray.add(lbl);
        fields.add("labels", labelArray);

        subtask.add("fields", fields);
        return subtask;
    }

    @Test
    void testDeterminarTipoSubtask() {
        assertEquals("Visado", DmJiraValidationMethods.determinarTipoSubtask("Tarea de Visado urgente"));
        assertEquals("Volcado", DmJiraValidationMethods.determinarTipoSubtask("Iniciar volcado de datos"));
        assertEquals("Correcion", DmJiraValidationMethods.determinarTipoSubtask("Petición de correccion de bug"));
        assertEquals("Otro", DmJiraValidationMethods.determinarTipoSubtask("Resumen general"));
    }

    @Test
    void testObtenerLabelEsperado() {
        assertEquals("#TTV_Visado", DmJiraValidationMethods.obtenerLabelEsperado("Visado"));
        assertEquals("#TTV_Dictamen", DmJiraValidationMethods.obtenerLabelEsperado("Volcado"));
        assertEquals("#TTV_Correccion", DmJiraValidationMethods.obtenerLabelEsperado("Correcion"));
        assertEquals("#TTV_*", DmJiraValidationMethods.obtenerLabelEsperado("Otro"));
    }

    @Test
    void testContieneLabelTipoAndDM() {
        List<String> labels = List.of("#TTV_Visado", "otro", "VISADO_DM");
        assertTrue(DmJiraValidationMethods.contieneLabelTipo(labels, "Visado"));
        assertTrue(DmJiraValidationMethods.contieneLabelDM(labels));

        labels = List.of("#TTV_Dictamen", "dato_DM");
        assertTrue(DmJiraValidationMethods.contieneLabelTipo(labels, "Volcado"));
        assertTrue(DmJiraValidationMethods.contieneLabelDM(labels));

        labels = List.of("sin_sufijo", "#TTV_Correccion");
        assertTrue(DmJiraValidationMethods.contieneLabelTipo(labels, "Correcion"));
        assertFalse(DmJiraValidationMethods.contieneLabelDM(labels));
    }

    @Test
    void testValidarSubtareaSinErrores() {
        JsonObject subtask = buildSubtask(
                "Visado OK", "Sub-task", "Medium", "New", "2461936", "Descripción válida",
                new String[]{"#TTV_Visado", "algo_DM"}
        );
        List<String> errores = DmJiraValidationMethods.validarSubtarea(subtask);
        assertTrue(errores.isEmpty(), "No deberían surgir errores");
    }

    @Test
    void testValidarSubtareaConErrores() {
        JsonObject subtask = buildSubtask(
                "Resumen", "Bug", "High", "Deployed", "000000", "   ",
                new String[]{"label1"}
        );
        List<String> errores = DmJiraValidationMethods.validarSubtarea(subtask);
        assertFalse(errores.isEmpty());
        assertTrue(errores.contains("tipo incorrecto"));
        assertTrue(errores.contains("prioridad incorrecta"));
        assertTrue(errores.contains("status inválido: se esperaba 'New'"));
        assertTrue(errores.contains("Team Backlog distinto"));
        assertTrue(errores.contains("descripción vacía"));
        assertTrue(errores.contains("falta label tipo"));
        assertTrue(errores.contains("falta label _DM"));
    }

    @Test
    void testObtenerDetallesValidacion() {
        JsonObject subtask = buildSubtask(
                "Volcado tarea", "Sub-task", "Medium", "New", "2461936", "OK",
                new String[]{"#TTV_Dictamen", "x_DM"}
        );
        List<String> detalles = DmJiraValidationMethods.obtenerDetallesValidacion(subtask);
        assertEquals(8, detalles.size());
        assertTrue(detalles.get(0).startsWith("Correcto"));
        assertTrue(detalles.get(1).contains("Tipo Subtarea"));
        assertTrue(detalles.get(6).contains("Label tipo presente"));
        assertTrue(detalles.get(7).contains("Sufijo label _DM"));
    }
}