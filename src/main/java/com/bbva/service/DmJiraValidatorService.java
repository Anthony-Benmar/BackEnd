package com.bbva.service;

import com.bbva.dao.DmJiraValidatorLogDao;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.DmJiraValidatorMessageDTO;
import com.bbva.entities.jiravalidator.JiraValidatorLogEntity;
import com.bbva.util.ApiJiraMet.DmJiraValidationMethods;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DmJiraValidatorService {

    private final DmJiraValidatorLogDao logDao = DmJiraValidatorLogDao.getInstance();

    public List<DmJiraValidatorMessageDTO> validateHistoriaDM(JiraValidatorByUrlRequest dto, JsonObject ticketMetadata, Map<String, JsonObject> subtaskMetadataMap) {

        List<DmJiraValidatorMessageDTO> messages = new ArrayList<>();
        JiraValidatorLogEntity logEntity = new JiraValidatorLogEntity();

        logEntity.setFecha(LocalDateTime.now());
        logEntity.setUsuario(dto.getUserName());
        logEntity.setNombre(dto.getName());
        logEntity.setTicket(dto.getUrlJira());

        String issueType = ticketMetadata.getAsJsonObject("fields").get("issuetype").getAsJsonObject().get("name").getAsString();
        String teamBacklog = ticketMetadata.getAsJsonObject("fields").getAsJsonArray("customfield_13300").get(0).getAsString();

        messages.add(buildMessage(1, "Validación Issue Type", issueType.equalsIgnoreCase("Story"), issueType.equalsIgnoreCase("Story") ? "Correcto: Issue Type Story" : "Incorrecto: Issue Type no es Story", null));
        logEntity.setRegla1(issueType.equalsIgnoreCase("Story") ? "1" : "0");

        messages.add(buildMessage(2, "Validación Team Backlog", teamBacklog.equals("2461936"), teamBacklog.equals("2461936") ? "Correcto: Team Backlog PE DATA MODELLING" : "Incorrecto: Team Backlog distinto a PE DATA MODELLING", null));
        logEntity.setRegla2(teamBacklog.equals("2461936") ? "1" : "0");

        JsonArray subtasks = ticketMetadata.getAsJsonObject("fields").getAsJsonArray("subtasks");
        int reglaIndex = 3;

        for (JsonElement subtaskElem : subtasks) {
            String subtaskKey = subtaskElem.getAsJsonObject().get("key").getAsString();
            JsonObject subtaskMetadata = subtaskMetadataMap.get(subtaskKey).getAsJsonArray("issues").get(0).getAsJsonObject();
            String summary = subtaskMetadata.getAsJsonObject("fields").get("summary").getAsString().toUpperCase();

            List<String> errores = DmJiraValidationMethods.validarSubtarea(subtaskMetadata);
            List<String> detalles = DmJiraValidationMethods.obtenerDetallesValidacion(subtaskMetadata);

            boolean isValidSubtask = errores.isEmpty();
            String resumen = isValidSubtask
                    ? "Correcto: La subtarea \"" + summary + "\" cumple con todos los criterios."
                    : "Incorrecto: La subtarea \"" + summary + "\" no cumple con todos los criterios.";

            messages.add(buildMessage(reglaIndex++, "Validación subtarea " + subtaskKey, isValidSubtask, resumen, detalles));
        }

        logDao.insertDmJiraValidatorLog(logEntity);
        return messages;
    }

    private DmJiraValidatorMessageDTO buildMessage(int ruleId, String rule, boolean isValid, String message, List<String> details) {
        return DmJiraValidatorMessageDTO.builder()
                .ruleId(ruleId)
                .rule(rule)
                .message(message)
                .details(details)
                .status(isValid ? "success" : "error")
                .visible(true)
                .order(ruleId)
                .build();
    }
}
