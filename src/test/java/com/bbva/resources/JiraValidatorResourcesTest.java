package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.DmJiraValidatorResponseDTO;
import com.bbva.dto.jira.response.JiraResponseDTO;
import com.bbva.service.DmJiraValidatorService;
import com.bbva.service.JiraValidatorService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.Mockito;


import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JiraValidatorResourcesTest {

    private JiraValidatorService jiraValidatorServiceMock;
    private JiraValidatorResources jiraValidatorResources;

    @BeforeEach
    void setUp() throws Exception {
        jiraValidatorResources = new JiraValidatorResources();
        jiraValidatorServiceMock = Mockito.mock(JiraValidatorService.class);
        Field serviceField = JiraValidatorResources.class.getDeclaredField("jiraValidatorService");
        serviceField.setAccessible(true);
        serviceField.set(jiraValidatorResources, jiraValidatorServiceMock);
    }

    @Test
    void testValidatorByUrl() throws Exception {
        JiraValidatorByUrlRequest mockRequest = new JiraValidatorByUrlRequest();
        when(jiraValidatorServiceMock.getValidatorByUrl(mockRequest)).thenReturn(getResponseDto());
        IDataResult<JiraResponseDTO> response = jiraValidatorResources.validatorByUrl(mockRequest);

        assertEquals(String.valueOf(Response.Status.OK.getStatusCode()), response.status);
    }

    private IDataResult<JiraResponseDTO> getResponseDto(){
        String reponse = "{\n" +
                "    \"data\": {\n" +
                "        \"data\": [\n" +
                "            {\n" +
                "                \"ruleId\": 1,\n" +
                "                \"rule\": \"Validacion Summary HUT Type:\",\n" +
                "                \"message\": \"Tipo de desarrollo: ingesta valido para el summary\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 2,\n" +
                "                \"rule\": \"Validacion Issue Type:\",\n" +
                "                \"message\": \"Issue Type: Story\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 2\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 3,\n" +
                "                \"rule\": \"Validacion Fix Version:\",\n" +
                "                \"message\": \"Esta regla no es válida para este tipo de desarrollo.\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 3\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 4,\n" +
                "                \"rule\": \"Validacion Labels:\",\n" +
                "                \"message\": \"Todas las etiquetas correspondientes fueron encontradas: ReleasePRDatio\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 4\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 5,\n" +
                "                \"rule\": \"Validacion Tablero Proyecto:\",\n" +
                "                \"message\": \"El tablero no se ha encontrado como válido para los proyectos habilitados del Q\",\n" +
                "                \"status\": \"error\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 5\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 6,\n" +
                "                \"rule\": \"Validacion Asignacion a Tablero de DQA:\",\n" +
                "                \"message\": \"Asignado a Tablero de DQA\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 6\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 7,\n" +
                "                \"rule\": \"Validacion Tablero DQA:\",\n" +
                "                \"message\": \"Se creó en tablero diferente a DQA: Peru - PER DAT Local Ingestor Team 2.\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 7\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 8,\n" +
                "                \"rule\": \"Validacion Feature Link:\",\n" +
                "                \"message\": \"https://jira.globaldevtools.bbva.com/browse/DEDATIOEN4-6710 asociado correctamente\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 8\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 9,\n" +
                "                \"rule\": \"Validacion Feature Link Status:\",\n" +
                "                \"message\": \"Con estado Deployed\",\n" +
                "                \"status\": \"error\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 9\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 10,\n" +
                "                \"rule\": \"Validacion Feature Link Program Increment:\",\n" +
                "                \"message\": \"Con Program Increment [\\\"2025-Q1\\\"]\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 10\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 11,\n" +
                "                \"rule\": \"Validacion Feature Link Incidencia/problema:\",\n" +
                "                \"message\": \"Esta regla no es válida para este tipo de desarrollo.\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 11\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 12,\n" +
                "                \"rule\": \"Validacion Item Type:\",\n" +
                "                \"message\": \"Se encontró Item Type correcto Technical\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 12\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 13,\n" +
                "                \"rule\": \"Validacion Tech Stack:\",\n" +
                "                \"message\": \"Se encontró Tech Stack correcto Data - Dataproc\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 13\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 14,\n" +
                "                \"rule\": \"Validacion Acceptance Criteria:\",\n" +
                "                \"message\": \"Es válido: Desarrollo según lineamientos globales ONE y de Data Quality Assurance Perú.\",\n" +
                "                \"status\": \"error\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 14\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 15,\n" +
                "                \"rule\": \"Validacion Impact Label:\",\n" +
                "                \"message\": \"Esta regla no es válida para este tipo de desarrollo.\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 15\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 16,\n" +
                "                \"rule\": \"Validacion documentos adjuntos:\",\n" +
                "                \"message\": \"Todos los adjuntos requeridos fueron encontrados: C204\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 16\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 17,\n" +
                "                \"rule\": \"Validacion Dependencias:\",\n" +
                "                \"message\": \"Las siguientes dependencias: https://jira.globaldevtools.bbva.com/browse/DEDATIOEN4-6872 no se encuentran en el estado correspondiente (In Progress).\",\n" +
                "                \"status\": \"warning\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 17\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 18,\n" +
                "                \"rule\": \"Validacion Dependencias - Feature Dependencia vs Ticket:\",\n" +
                "                \"message\": \"Todas las dependencias tienen el mismo feature link\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 18\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 19,\n" +
                "                \"rule\": \"Validacion Dependencias - Comprometida por QE:\",\n" +
                "                \"message\": \"Dependencia cuenta con comentario \\\"Comprometido\\\", pero no de algún QE o del QE temporal.\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 19\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 20,\n" +
                "                \"rule\": \"Validacion Subtareas:\",\n" +
                "                \"message\": \"Todas las subtareas requeridas fueron encontradas: [C204][PO], [C204][QA]\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 20\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 21,\n" +
                "                \"rule\": \"Validacion Subtareas Status:\",\n" +
                "                \"message\": \"Todas las subtareas tienen el estado Aceptado[C204][PO], [VB][KM], [VB][SO]\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 21\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 22,\n" +
                "                \"rule\": \"Validacion Subtareas VoBo:\",\n" +
                "                \"message\": \"No se encontró persona para este rol para [C204][PO] en SIDE\",\n" +
                "                \"status\": \"error\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 22\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 23,\n" +
                "                \"rule\": \"Validacion Subtareas Contractor:\",\n" +
                "                \"message\": \"Subtarea valida [C204][PO] asignada a carlos.jimenez.aguilar@bbva.com es Interno BBVA.\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 23\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 24,\n" +
                "                \"rule\": \"Validacion Subtarea Alpha:\",\n" +
                "                \"message\": \"Esta regla no es válida para este tipo de desarrollo.\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 24\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 25,\n" +
                "                \"rule\": \"Validacion Status JIRA:\",\n" +
                "                \"message\": \"Con estado Deployed\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 25\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 26,\n" +
                "                \"rule\": \"Validacion PR:\",\n" +
                "                \"message\": \"No se detectó una PR valida asociada. Atención: Si la PR fue asociada correctamente, falta dar permisos de acceso a los QEs. Atención: Se encontraron 1 PRs asociadas en MERGED: [https://bitbucket.globaldevtools.bbva.com/bitbucket/projects/PE_PCTD_APP-ID-20768_DSG/repos/vbsuu_skynet_pctd/pull-requests/210]\",\n" +
                "                \"status\": \"warning\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 26\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 27,\n" +
                "                \"rule\": \"Validacion PR Rama Destino:\",\n" +
                "                \"message\": \"Se encontró PR branch destino correcta: master\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 27\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 28,\n" +
                "                \"rule\": \"Validacion de productivizacion:\",\n" +
                "                \"message\": \"Esta regla no es válida para este tipo de desarrollo.\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 28\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 29,\n" +
                "                \"rule\": \"Validacion ticket de integracion:\",\n" +
                "                \"message\": \"No es ticket de integración\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 29\n" +
                "            },\n" +
                "            {\n" +
                "                \"ruleId\": 30,\n" +
                "                \"rule\": \"Advertencia IFRS9: Se alerta sobre la fecha de los bloqueos correspondientes a IFRS9\",\n" +
                "                \"message\": \"No se encontraron advertencias relacionadas con la fecha.\",\n" +
                "                \"status\": \"success\",\n" +
                "                \"visible\": true,\n" +
                "                \"order\": 30\n" +
                "            }\n" +
                "        ],\n" +
                "        \"successCount\": 24,\n" +
                "        \"warningCount\": 2,\n" +
                "        \"errorCount\": 4\n" +
                "    },\n" +
                "    \"success\": true,\n" +
                "    \"message\": \"Reglas de validacion\",\n" +
                "    \"status\": \"200\"\n" +
                "}";
        Gson gson = new Gson();
        Type type = new TypeToken<IDataResult<JiraResponseDTO>>() {}.getType();
        return gson.fromJson(reponse, type);
    }

    @Test
    void testValidatorDataModelling() throws Exception {
        // Arrange - Request DTO
        JiraValidatorByUrlRequest request = new JiraValidatorByUrlRequest();
        request.setUrlJira("https://jira.globaldevtools.bbva.com/browse/DEDATIOEN4-1234");
        request.setUserName("york.yusel.contractor");
        request.setToken("xxxx");
        request.setName("York");

        JiraValidatorService jiraServiceMock = mock(JiraValidatorService.class);
        DmJiraValidatorService dmServiceMock = mock(DmJiraValidatorService.class);

        JiraValidatorResources resource = new JiraValidatorResources();
        setPrivateField(resource, "jiraValidatorService", jiraServiceMock);
        setPrivateField(resource, "dmJiraValidatorService", dmServiceMock);

        JsonObject ticketMetadata = new JsonObject();
        JsonObject issue = new JsonObject();
        JsonObject fields = new JsonObject();
        fields.add("subtasks", new JsonArray());
        issue.add("fields", fields);
        JsonArray issues = new JsonArray();
        issues.add(issue);
        ticketMetadata.add("issues", issues);

        when(jiraServiceMock.getMetadataIssues(eq(request), anyList())).thenReturn(ticketMetadata);
        when(jiraServiceMock.buildSubtaskMetadataMap(eq(request), any())).thenReturn(Collections.emptyMap());
        when(dmServiceMock.validateHistoriaDM(request)).thenReturn(List.of());

        // Act
        IDataResult<DmJiraValidatorResponseDTO> result = resource.validatorDataModelling(request);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof SuccessDataResult);
        assertEquals("200", result.status);
        assertEquals("Validación Data Modelling ejecutada", result.message);
        assertEquals(0, result.data.getData().size());
        assertEquals(0, result.data.getErrorCount());
        assertEquals(0, result.data.getSuccessCount());
        assertEquals(0, result.data.getWarningCount());
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

}
