package com.bbva.service;

import com.bbva.dao.DmJiraValidatorLogDao;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.DmJiraValidatorMessageDTO;
import com.bbva.entities.jiravalidator.JiraValidatorLogEntity;
import com.bbva.util.apijiramet.DmJiraValidationMethods;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DmJiraValidatorService {

    private static final String ISSUE_TYPE_STORY = "Story";
    private static final String TEAM_BACKLOG_PE_DATA_MODELLING = "2461936";

    private final DmJiraValidatorLogDao logDao = DmJiraValidatorLogDao.getInstance();
    private final JiraApiService jiraApiService = new JiraApiService();

    public List<DmJiraValidatorMessageDTO> validateHistoriaDM(JiraValidatorByUrlRequest dto) throws Exception {
        List<DmJiraValidatorMessageDTO> messages = new ArrayList<>();
        JiraValidatorLogEntity logEntity = new JiraValidatorLogEntity();

        logEntity.setFecha(LocalDateTime.now());
        logEntity.setUsuario(dto.getUserName());
        logEntity.setNombre(dto.getName());
        logEntity.setTicket(dto.getUrlJira());

        String ticketKey = extractIssueKey(dto.getUrlJira());

        JsonObject ticketMetadata = getIssueMetadata(dto, ticketKey);
        JsonObject fields = ticketMetadata.getAsJsonObject("fields");

        String issueType = fields.getAsJsonObject("issuetype").get("name").getAsString();
        String teamBacklog = fields.getAsJsonArray("customfield_13300").get(0).getAsString();

        boolean isStory = ISSUE_TYPE_STORY.equalsIgnoreCase(issueType.trim());
        boolean isCorrectBacklog = TEAM_BACKLOG_PE_DATA_MODELLING.equals(teamBacklog.trim());

        messages.add(buildMessage(1, "Validación Issue Type",
                isStory,
                isStory ? "Correcto: Issue Type Story" : "Incorrecto: Issue Type no es Story",
                null));
        logEntity.setRegla1(isStory ? "1" : "0");

        messages.add(buildMessage(2, "Validación Team Backlog",
                isCorrectBacklog,
                isCorrectBacklog ? "Correcto: Team Backlog PE DATA MODELLING" : "Incorrecto: Team Backlog distinto a PE DATA MODELLING",
                null));
        logEntity.setRegla2(isCorrectBacklog ? "1" : "0");

        JsonArray subtasks = fields.getAsJsonArray("subtasks");
        int reglaIndex = 3;

        for (JsonElement subtaskElem : subtasks) {
            String subtaskKey = subtaskElem.getAsJsonObject().get("key").getAsString();
            JsonObject subtaskMetadata = getIssueMetadata(dto, subtaskKey);

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

    protected JsonObject getIssueMetadata(JiraValidatorByUrlRequest dto, String issueKey) throws Exception {
        return jiraApiService.getIssueMetadata(dto, issueKey);
    }

    private String extractIssueKey(String urlOrKey) {
        if (urlOrKey == null) return null;
        if (urlOrKey.contains("/browse/")) {
            String[] parts = urlOrKey.split("/browse/");
            return parts.length > 1 ? parts[1] : null;
        }
        return urlOrKey;
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
