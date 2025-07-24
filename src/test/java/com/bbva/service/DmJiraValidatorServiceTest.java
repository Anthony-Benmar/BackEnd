package com.bbva.service;

import com.bbva.dao.DmJiraValidatorLogDao;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.DmJiraValidatorMessageDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DmJiraValidatorServiceTest {

    private DmJiraValidatorService service;

    @BeforeEach
    void setUp() {
        service = new DmJiraValidatorService();
    }

    @Test
    void testValidateHistoriaDM_withOneInvalidSubtask() {
        // Mock DAO para evitar escritura real
        DmJiraValidatorLogDao mockDao = mock(DmJiraValidatorLogDao.class);
        MockedStatic<DmJiraValidatorLogDao> mockedStatic = Mockito.mockStatic(DmJiraValidatorLogDao.class);
        mockedStatic.when(DmJiraValidatorLogDao::getInstance).thenReturn(mockDao);
        when(mockDao.insertDmJiraValidatorLog(any())).thenReturn(true);

        // Armar ticketMetadata
        JsonObject ticketMetadata = new JsonObject();
        JsonObject fields = new JsonObject();
        fields.add("issuetype", tipo("Story"));
        fields.add("customfield_13300", array("2461936")); // backlog correcto
        fields.add("subtasks", arraySubtask("DEDATIOEN4-9999"));
        ticketMetadata.add("fields", fields);

        // Armar subtarea mock
        JsonObject subtaskFields = new JsonObject();
        subtaskFields.addProperty("summary", "CS - VOLCADO BUI DM"); // Contiene "Volcado"
        subtaskFields.add("issuetype", tipo("Bug")); // Error: no es Sub-task
        subtaskFields.add("priority", tipo("Low")); // Error: no es Medium
        subtaskFields.add("status", tipo("Deployed")); // Error: debe ser New
        subtaskFields.add("customfield_13300", array("9999999")); // Error: backlog distinto
        subtaskFields.addProperty("description", ""); // Error: vacía
        subtaskFields.add("labels", array("#TTV_Dictamen", "algo_DM")); // Etiquetas correctas

        JsonObject subtaskIssue = new JsonObject();
        subtaskIssue.add("fields", subtaskFields);

        JsonArray subtaskIssuesArray = new JsonArray();
        subtaskIssuesArray.add(subtaskIssue);

        JsonObject subtaskWrapper = new JsonObject();
        subtaskWrapper.add("issues", subtaskIssuesArray);

        Map<String, JsonObject> subtaskMetadataMap = new HashMap<>();
        subtaskMetadataMap.put("DEDATIOEN4-9999", subtaskWrapper);

        // DTO
        JiraValidatorByUrlRequest dto = new JiraValidatorByUrlRequest();
        dto.setUrlJira("https://jira.globaldevtools.bbva.com/browse/DEDATIOEN4-1234");
        dto.setUserName("york.yusel.contractor");
        dto.setToken("xxxx");
        dto.setName("York");

        // Ejecutar
        List<DmJiraValidatorMessageDTO> messages = service.validateHistoriaDM(dto, ticketMetadata, subtaskMetadataMap);

        // Validaciones
        assertEquals(3, messages.size()); // tipo + backlog + subtarea
        assertEquals("success", messages.get(0).getStatus()); // tipo story
        assertEquals("success", messages.get(1).getStatus()); // backlog correcto
        assertEquals("error", messages.get(2).getStatus()); // subtarea falla
        assertTrue(messages.get(2).getMessage().contains("no cumple"));
        assertNotNull(messages.get(2).getDetails());

        mockedStatic.close();
    }

    // Métodos utilitarios para armar JSON
    private JsonObject tipo(String name) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", name);
        return obj;
    }

    private JsonArray array(String... values) {
        JsonArray arr = new JsonArray();
        for (String val : values) arr.add(val);
        return arr;
    }

    private JsonArray arraySubtask(String... keys) {
        JsonArray arr = new JsonArray();
        for (String key : keys) {
            JsonObject o = new JsonObject();
            o.addProperty("key", key);
            arr.add(o);
        }
        return arr;
    }
}