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
        JiraValidatorLogEntity logEntity = buildInitialLog(dto);

        String ticketKey = extractIssueKey(dto.getUrlJira());
        JsonObject fields = getIssueMetadata(dto, ticketKey).getAsJsonObject("fields");

        boolean isStory = isIssueTypeStory(fields);
        boolean isCorrectBacklog = isTeamBacklogCorrect(fields);

        messages.add(buildMessage(1, "Validación Issue Type", isStory, isStory ? "Correcto: Issue Type Story" : "Incorrecto: Issue Type no es Story", null));
        logEntity.setRegla1(isStory ? "1" : "0");

        messages.add(buildMessage(2, "Validación Team Backlog", isCorrectBacklog, isCorrectBacklog ? "Correcto: Team Backlog PE DATA MODELLING" : "Incorrecto: Team Backlog distinto a PE DATA MODELLING", null));
        logEntity.setRegla2(isCorrectBacklog ? "1" : "0");

        validateSubtasks(fields.getAsJsonArray("subtasks"), dto, messages);

        logDao.insertDmJiraValidatorLog(logEntity);
        return messages;
    }

    private void validateSubtasks(JsonArray subtasks, JiraValidatorByUrlRequest dto, List<DmJiraValidatorMessageDTO> messages) throws Exception {
        int ruleIndex = 3;
        for (JsonElement subtaskElem : subtasks) {
            String subtaskKey = subtaskElem.getAsJsonObject().get("key").getAsString();
            JsonObject subtask = getIssueMetadata(dto, subtaskKey);

            String summary = subtask.getAsJsonObject("fields").get("summary").getAsString().toUpperCase();
            List<String> errores = DmJiraValidationMethods.validarSubtarea(subtask);
            List<String> detalles = DmJiraValidationMethods.obtenerDetallesValidacion(subtask);

            boolean isValid = errores.isEmpty();
            String resumen = isValid
                    ? "Correcto: La subtarea \"" + summary + "\" cumple con todos los criterios."
                    : "Incorrecto: La subtarea \"" + summary + "\" no cumple con todos los criterios.";

            messages.add(buildMessage(ruleIndex++, "Validación subtarea " + subtaskKey, isValid, resumen, detalles));
        }
    }

    protected JsonObject getIssueMetadata(JiraValidatorByUrlRequest dto, String issueKey) throws Exception {
        return jiraApiService.getIssueMetadata(dto, issueKey);
    }

    private boolean isIssueTypeStory(JsonObject fields) {
        String issueType = fields.getAsJsonObject("issuetype").get("name").getAsString();
        return ISSUE_TYPE_STORY.equalsIgnoreCase(issueType.trim());
    }

    private boolean isTeamBacklogCorrect(JsonObject fields) {
        JsonElement backlogElement = fields.get("customfield_13300");
        if (backlogElement != null && backlogElement.isJsonArray()) {
            JsonArray array = backlogElement.getAsJsonArray();
            return array.size() > 0 && TEAM_BACKLOG_PE_DATA_MODELLING.equals(array.get(0).getAsString().trim());
        }
        return false;
    }

    private JiraValidatorLogEntity buildInitialLog(JiraValidatorByUrlRequest dto) {
        JiraValidatorLogEntity log = new JiraValidatorLogEntity();
        log.setFecha(LocalDateTime.now());
        log.setUsuario(dto.getUserName());
        log.setNombre(dto.getName());
        log.setTicket(dto.getUrlJira());
        return log;
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