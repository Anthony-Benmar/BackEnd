package com.bbva.util.ApiJiraMet;

import com.bbva.dao.InfoJiraProjectDao;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.entities.jiravalidator.InfoJiraProject;
import com.bbva.service.JiraApiService;
import com.bbva.util.ApiJiraName;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.bbva.common.jiraValidador.JiraValidatorConstantes.*;

public class JiraValidationMethods {
    private static final Logger LOGGER = Logger.getLogger(JiraValidationMethods.class.getName());
    private final String jiraCode;
    private final JsonObject jiraTicketResult;
    private boolean isInTableroDQA;
    private final String featureLinkCode;
    private final JsonObject featureLinkResult;
    private final String currentQ;
    private final String teamBackLogDQAId = "2461905";

    public JiraValidationMethods(String jiraCode, JsonObject jiraTicketResult, String featureLinkCode, JsonObject featureLinkResult) {
        this.jiraCode = jiraCode;
        this.jiraTicketResult = jiraTicketResult;
        this.featureLinkCode = featureLinkCode;
        this.featureLinkResult = featureLinkResult;
        this.isInTableroDQA = false;
        this.currentQ = getCurrentQ();
    }

    public String getCurrentQ(){
        return InfoJiraProjectDao.getInstance().currentQ();
    }

    public Map<String, Object> getValidatorValidateSummaryHUTType(String helpMessage, String group) {
        String message;
        boolean isValid;
        boolean isWarning = false;
        String tipoDesarrolloSummary = "";
        String summaryComparacion = extractLabel(jiraTicketResult).toLowerCase();

        for (Map.Entry<String, List<String>> entry : DEVELOPS_TYPES.entrySet()) {
            String tipoDesarrolloKey = entry.getKey();
            List<String> tipoDesarrolloItem = entry.getValue();

            if (tipoDesarrolloItem.stream().anyMatch(validacionText -> summaryComparacion.contains(validacionText.toLowerCase()))) {
                tipoDesarrolloSummary = tipoDesarrolloKey;
                break;
            }
        }

        if (!tipoDesarrolloSummary.isEmpty()) {
            message = "Tipo de desarrollo: " + tipoDesarrolloSummary + " valido para el summary";

            isValid = true;
        } else {
            message = "Summary sin Tipo de desarrollo valido";
            isValid = false;
        }
        return Map.of(
                MESSAGE, message,
                ISVALID, isValid,
                ISWARNING, isWarning,
                HELPMESSAGE, helpMessage,
                GROUP, group,
                "tipoDesarrolloSummary", tipoDesarrolloSummary
        );
    }

    public Map<String, Object> getValidatorIssueType(String tipoDesarrollo,String helpMessage, String group) {
        String issueType = extractIssueType(jiraTicketResult);

        String message;
        boolean isValid;
        boolean isWarning = false;
        List<String> types = TICKET_HU_TYPES.get(tipoDesarrollo);
        if (types.contains(issueType)) {
            message = "Issue Type: " + issueType;
            isValid = true;
        } else {
            message = "Issue Type inválido: " + issueType;
            message = message + " Atención: Solo se aceptan los siguientes Issue Types: " + String.join(", ",types);
            isValid = false;
        }
        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidatorValidateHUTType(
            String teamBackLogId, List<String> teamBackLogTicketIdRLB, String helpMessage,
            String tipoDesarrollo, String group) {

        boolean isWarning = false;

        if (!tipoDesarrollo.equalsIgnoreCase(INGESTA)) {
            return buildValidationResult(MSG_RULE_INVALID, true, isWarning, helpMessage, group);
        }

        if (teamBackLogTicketIdRLB.contains(teamBackLogId)) {
            return buildValidationResult(MSG_RULE_INVALID, true, isWarning, helpMessage, group);
        }

        JsonArray issuelinks = jiraTicketResult.getAsJsonObject(FIELDS).getAsJsonArray(ISSUELINKS);
        String name = getIssueTypeName(issuelinks);
        String statusCategory = getIssueStatusCategory(issuelinks);

        if (name == null || !name.equals(STORY)) {
            return buildValidationResult("No es ticket de integración", true, isWarning, helpMessage, group);
        }

        if (statusCategory != null && statusCategory.equals(DEPLOYED)) {
            return buildValidationResult("Ticket de integración sin tickets deployados", true, isWarning, helpMessage, group);
        }

        return buildValidationResult("Ticket de integración sin tickets deployados", false, isWarning, helpMessage, group);
    }

    private String getIssueTypeName(JsonArray issuelinks) {
        for (JsonElement issueLinkElement : issuelinks) {
            JsonObject issueLink = issueLinkElement.getAsJsonObject();
            String inward = issueLink.getAsJsonObject(TYPE).get(INWARD).getAsString();

            if (inward.equalsIgnoreCase(IS_CHILD_ITEM_OF) && issueLink.has(INWARD_ISSUE)) {
                JsonObject inwardIssue = issueLink.getAsJsonObject(INWARD_ISSUE);
                if (inwardIssue.has(FIELDS)) {
                    JsonObject fields = inwardIssue.getAsJsonObject(FIELDS);
                    if (fields.has(ISSUETYPE)) {
                        JsonObject issuetype = fields.getAsJsonObject(ISSUETYPE);
                        return issuetype.get(NAME).getAsString();
                    }
                }
            }
        }
        return null;
    }

    private String getIssueStatusCategory(JsonArray issuelinks) {
        for (JsonElement issueLinkElement : issuelinks) {
            JsonObject issueLink = issueLinkElement.getAsJsonObject();
            String inward = issueLink.getAsJsonObject(TYPE).get(INWARD).getAsString();

            if (inward.equalsIgnoreCase(IS_CHILD_ITEM_OF) && issueLink.has(INWARD_ISSUE)) {
                JsonObject inwardIssue = issueLink.getAsJsonObject(INWARD_ISSUE);
                if (inwardIssue.has(FIELDS)) {
                    JsonObject fields = inwardIssue.getAsJsonObject(FIELDS);
                    if (fields.has(STATUS)) {
                        JsonObject status = fields.getAsJsonObject(STATUS);
                        return status.get(NAME).getAsString();
                    }
                }
            }
        }
        return null;
    }


    private Map<String, Object> buildValidationResult(String message, boolean isValid, boolean isWarning, String helpMessage, String group) {
        return Map.of(
                MESSAGE, message,
                ISVALID, isValid,
                ISWARNING, isWarning,
                HELPMESSAGE, helpMessage,
                GROUP, group
        );
    }

    public Map<String, Object> getValidationPR(String tipoDesarrollo, String helpMessage, String group) {
        String message;
        boolean isValid;
        boolean isWarning = false;

        List<String> prsStatusException = List.of("DECLINED");
        List<String> prsStatusWarning = List.of("MERGED");

        JsonObject jiraTicketResultPrs = jiraTicketResult.getAsJsonObject(FIELDS);

        Map<String, Map<String, String>> prsClassification = classifyPRs(jiraTicketResultPrs, prsStatusException, prsStatusWarning);

        Map<String, String> prWarning = prsClassification.get("warning");
        Map<String, String> prValid = prsClassification.get("valid");

        int cantidadPrsValidas = prValid.size();
        int cantidadPrsWarning = prWarning.size();

        List<String> tipoDesarrolloPRs = getDevelopmentTypes();
        if (isValidDevelopmentType(tipoDesarrollo, tipoDesarrolloPRs)) {
            message = generateValidationMessage(cantidadPrsValidas, cantidadPrsWarning, prValid, prWarning);
            isValid = cantidadPrsValidas == 1;
            isWarning = cantidadPrsWarning > 0;
        } else {
            if (cantidadPrsValidas == 0) {
                message = "Sin PR asociada.";
                isValid = true;
            } else {
                message = "No se debe asociar una PR a este Tipo de Desarrollo.";
                isValid = false;
            }
        }

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    private boolean isValidDevelopmentType(String tipoDesarrollo, List<String> tipoDesarrolloPRs) {
        return tipoDesarrollo.equalsIgnoreCase("PRs") || tipoDesarrollo.equalsIgnoreCase(MALLAS) || tipoDesarrolloPRs.contains(tipoDesarrollo.toLowerCase());
    }

    private String generateValidationMessage(int cantidadPrsValidas, int cantidadPrsWarning, Map<String, String> prValid, Map<String, String> prWarning) {
        StringBuilder message = new StringBuilder();

        if (cantidadPrsValidas == 1) {
            message.append("Con PR asociada: ").append(prValid.keySet());
        } else if (cantidadPrsValidas > 1) {
            message.append("Se encontraron ").append(cantidadPrsValidas).append(" PRs asociadas: ").append(prValid.keySet())
                    .append(". Atención: No se puede tener más de una PR asociada.");
        } else {
            message.append("No se detectó una PR valida asociada.")
                    .append(" Atención: Si la PR fue asociada correctamente, falta dar permisos de acceso a los QEs.");
        }

        if (cantidadPrsWarning > 0) {
            message.append(" Atención: Se encontraron ").append(cantidadPrsWarning)
                    .append(" PRs asociadas en MERGED: ").append(prWarning.keySet());
        }

        return message.toString();
    }

    private Map<String, Map<String, String>> classifyPRs(JsonObject jiraTicketResultPrs, List<String> prsStatusException, List<String> prsStatusWarning) {
        Map<String, String> prException = new HashMap<>();
        Map<String, String> prWarning = new HashMap<>();
        Map<String, String> prValid = new HashMap<>();
        Map<String, Map<String, String>> classification = new HashMap<>();

        if (jiraTicketResultPrs.get("prs") != null) {
            JsonArray prsArray = jiraTicketResultPrs.get("prs").getAsJsonArray();
            for (JsonElement prObj : prsArray) {
                JsonObject prJson = prObj.getAsJsonObject();
                String status = prJson.get(STATUS).getAsString();
                String url = prJson.get("url").getAsString();

                if (prsStatusException.contains(status)) {
                    prException.put(url, status);
                } else if (prsStatusWarning.contains(status)) {
                    prWarning.put(url, status);
                } else {
                    prValid.put(url, status);
                }
            }
        }

        classification.put("exception", prException);
        classification.put("warning", prWarning);
        classification.put("valid", prValid);
        return classification;
    }

    private List<String> getDevelopmentTypes() {
        return Stream.of("Procesamiento", "MigrationTool", "Hammurabi", "Ingesta", "Scaffolder", "Operativizacion",
                        "Teradata", "SmartCleaner", "SparkCompactor", "JSON Global")
                .map(String::toLowerCase)
                .toList();
    }

    public Map<String, Object> getValidationPRBranch(String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;
        List<String> validBranches = Arrays.asList("develop", "master");
        JsonObject jiraTicketResultPrs = jiraTicketResult
                .getAsJsonObject(FIELDS);

        List<String> tipoDesarrolloPRs = Arrays.asList("Procesamiento","MigrationTool",
                "Hammurabi", "Ingesta", "Scaffolder", "Operativizacion",
                "Teradata", "SmartCleaner","SparkCompactor", "JSON Global");

        tipoDesarrolloPRs.replaceAll(String::toLowerCase);

        if (jiraTicketResultPrs.get("prs") != null) {
            int cantidadPRs = jiraTicketResultPrs.get("prs").getAsJsonArray().size();
            if (cantidadPRs > 0) {
                for (JsonElement prObj : jiraTicketResultPrs.get("prs").getAsJsonArray()) {
                    String branch = prObj.getAsJsonObject().get("destinyBranch").getAsString();
                    if(validBranches.contains(branch)){
                        isValid = true;
                        message = "Se encontró PR branch destino correcta: "+branch;
                        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
                    }
                    else{
                        message = "No se encontró PR branch Asociada correcta: "+branch+ ". Solo son validas"+String.join(",",validBranches);
                    }
                }
            }
            else {
                message = "No se encontraron PRs asociadas";
            }
        }

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationValidateSubTask(String tipoDesarrollo, String helpMessage, String group) {
        List<String> aditionalSpecialSubtask = List.of(VB_KM, VB_SO);
        List<String> aditionalSpecialLabels = List.of("datioRutaCritica", "JobsHuerfanos");
        List<String> requiredSubTasks = SUBTASKS_BY_DEVELOP_TYPES.get(tipoDesarrollo);

        JsonArray labels = jiraTicketResult.getAsJsonObject(FIELDS).getAsJsonArray(LABELS);
        JsonArray subTasks = jiraTicketResult.getAsJsonObject(FIELDS).getAsJsonArray(SUBTASKS);

        List<String> foundSpecialLabel = extractMatchingLabels(labels, aditionalSpecialLabels);
        SubTaskResult subTaskResult = processSubTasks(subTasks, requiredSubTasks, foundSpecialLabel, aditionalSpecialSubtask);

        String message = generateMessageForSubTasks(tipoDesarrollo, subTaskResult, foundSpecialLabel, aditionalSpecialSubtask,requiredSubTasks);

        boolean isValid = new HashSet<>(subTaskResult.foundSubTasks).containsAll(requiredSubTasks) && !subTaskResult.hasMissingTasks;
        boolean isWarning = subTaskResult.hasWarnings;

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationValidateSubTaskStatus(String tipoDesarrollo, String helpMessage, String group) {
        List<String> results = new ArrayList<>(SUBTASKS_BY_DEVELOP_TYPES.get(tipoDesarrollo));
        results.addAll(List.of(VB_KM, VB_SO));
        if (tipoDesarrollo.equalsIgnoreCase(MALLAS) || tipoDesarrollo.equalsIgnoreCase("host")) {
            results.add("[VB][DEV]");
        }
        results.removeIf(subtask -> subtask.contains("QA"));

        JsonArray subTasks = jiraTicketResult.getAsJsonObject(FIELDS).getAsJsonArray(SUBTASKS);

        SubTaskStatusResult statusResult = validateSubTaskStatus(subTasks, results);

        String message = statusResult.hasBadStatus
                ? "Subtareas sin estado Accepted: " + String.join(", ", statusResult.badSubTaskLabels)
                : "Todas las subtareas tienen el estado Aceptado"+ String.join(", ", results);

        boolean isValid = !statusResult.hasBadStatus;

        return buildValidationResult(message, isValid, false, helpMessage, group);
    }

    private List<String> extractMatchingLabels(JsonArray labels, List<String> specialLabels) {
        List<String> matchingLabels = new ArrayList<>();
        for (JsonElement label : labels) {
            String labelString = label.getAsString();
            if (specialLabels.contains(labelString)) {
                matchingLabels.add(labelString);
            }
        }
        return matchingLabels;
    }

    private SubTaskResult processSubTasks(JsonArray subTasks, List<String> requiredSubTasks, List<String> foundSpecialLabel, List<String> aditionalSpecialSubtask) {
        List<String> foundSubTasks = new ArrayList<>();
        List<String> additionalSubTasks = new ArrayList<>();
        List<String> foundSpecialSubtasks = new ArrayList<>();
        boolean hasWarnings = false;
        boolean hasMissingTasks = false;

        for (JsonElement subTask : subTasks) {
            String subTaskLabel = extractLabel(subTask.getAsJsonObject());
            if (requiredSubTasks.contains(subTaskLabel)) {
                foundSubTasks.add(subTaskLabel);
            } else {
                additionalSubTasks.add(subTaskLabel);
                if (!foundSpecialLabel.isEmpty() || aditionalSpecialSubtask.contains(subTaskLabel)) {
                    foundSpecialSubtasks.add(subTaskLabel);
                }
            }
        }

        if (!new HashSet<>(foundSubTasks).containsAll(requiredSubTasks)) {
            hasMissingTasks = true;
        }

        return new SubTaskResult(foundSubTasks, additionalSubTasks, foundSpecialSubtasks, hasWarnings, hasMissingTasks);
    }

    private SubTaskStatusResult validateSubTaskStatus(JsonArray subTasks, List<String> validLabels) {
        List<JsonObject> validSubTasks = new ArrayList<>();
        List<String> badSubTaskLabels = new ArrayList<>();
        boolean hasBadStatus = false;

        for (JsonElement subTask : subTasks) {
            String subTaskLabel = extractLabel(subTask.getAsJsonObject());
            String subTaskStatus = extractStatus(subTask.getAsJsonObject());

            if (validLabels.contains(subTaskLabel) && !subTaskStatus.equalsIgnoreCase(ACCEPTED) && !subTaskStatus.equalsIgnoreCase(DISCARDED)) {
                badSubTaskLabels.add(subTaskLabel);
                hasBadStatus = true;
            }
        }

        return new SubTaskStatusResult(validSubTasks, badSubTaskLabels, hasBadStatus);
    }

    private String generateMessageForSubTasks(String tipoDesarrollo, SubTaskResult result, List<String> foundSpecialLabel, List<String> aditionalSpecialSubtask, List<String> requiredSubTasks) {
        StringBuilder message = new StringBuilder();

        if (new HashSet<>(result.foundSubTasks).containsAll(requiredSubTasks)) {
            message.append("Todas las subtareas requeridas fueron encontradas: ").append(String.join(", ", requiredSubTasks));
            if (!result.additionalSubTasks.isEmpty()) {
                message.append("También se encontraron subtareas adicionales: ").append(String.join(", ", result.additionalSubTasks)).append(". ");
                if (tipoDesarrollo.equals(MALLAS)) {
                    if (!foundSpecialLabel.isEmpty() && result.foundSpecialSubtasks.isEmpty()) {
                        message.append(MSG_RULE_NOSUBTAREA).append(String.join(", ", aditionalSpecialSubtask));
                    } else if (foundSpecialLabel.isEmpty() && !result.foundSpecialSubtasks.isEmpty()) {
                        message.append("Se recomienda validar las subtareas adicionales: ").append(String.join(", ", aditionalSpecialSubtask)).append(" para casos especiales.");
                    }
                }
            }
        } else {
            List<String> missingSubTasks = new ArrayList<>(requiredSubTasks);
            missingSubTasks.removeAll(result.foundSubTasks);
            message.append("Faltan las siguientes subtareas: ").append(String.join(", ", missingSubTasks)).append(". ");
        }

        return message.toString();
    }

    private static class SubTaskResult {
        List<String> foundSubTasks;
        List<String> additionalSubTasks;
        List<String> foundSpecialSubtasks;
        boolean hasWarnings;
        boolean hasMissingTasks;

        SubTaskResult(List<String> foundSubTasks, List<String> additionalSubTasks, List<String> foundSpecialSubtasks, boolean hasWarnings, boolean hasMissingTasks) {
            this.foundSubTasks = foundSubTasks;
            this.additionalSubTasks = additionalSubTasks;
            this.foundSpecialSubtasks = foundSpecialSubtasks;
            this.hasWarnings = hasWarnings;
            this.hasMissingTasks = hasMissingTasks;
        }
    }

    private static class SubTaskStatusResult {
        List<JsonObject> validSubTasks;
        List<String> badSubTaskLabels;
        boolean hasBadStatus;

        SubTaskStatusResult(List<JsonObject> validSubTasks, List<String> badSubTaskLabels, boolean hasBadStatus) {
            this.validSubTasks = validSubTasks;
            this.badSubTaskLabels = badSubTaskLabels;
            this.hasBadStatus = hasBadStatus;
        }
    }

    public Map<String, Object> getValidationValidateSubtaskPerson(Map<String, JsonObject> subtaskMetadataMap,
            String teamBackLogId, String helpMessage,
            String group, List<InfoJiraProject> infoJiraProjectList) {

        List<String> messageBadList = new ArrayList<>();
        List<String> messageGoodList = new ArrayList<>();
        boolean isValid = false;

        if (teamBackLogId == null || teamBackLogId.isEmpty()) {
            return buildValidationResult("HU sin Team BackLog", false, false, helpMessage, group);
        }

        JsonArray subTasks = jiraTicketResult.getAsJsonObject(FIELDS).getAsJsonArray(SUBTASKS);
        if (subTasks.isEmpty()) {
            return buildValidationResult("HU no cuenta con subtareas asociadas", false, false, helpMessage, group);
        }

        processSubtasksPerson(subtaskMetadataMap, subTasks, teamBackLogId, infoJiraProjectList, messageBadList, messageGoodList);

        if (messageBadList.isEmpty() && messageGoodList.isEmpty()) {
            messageBadList.add("No se encontraron subtareas asociadas");
        }

        if (messageBadList.isEmpty()) {
            isValid = true;
        }

        String message = String.join(". ", mergeMessageLists(messageGoodList, messageBadList));
        return buildValidationResult(message, isValid, false, helpMessage, group);
    }

    private void processSubtasksPerson(Map<String, JsonObject> subtaskMetadataMap,
                                       JsonArray subTasks, String teamBackLogId,
                                       List<InfoJiraProject> infoJiraProjectList,
                                       List<String> messageBadList, List<String> messageGoodList) {

        for (JsonElement subTask : subTasks) {
            String subtaskLabel = extractLabel(subTask);

            if (!shouldSkipSubtask(subtaskLabel, subTask)) {
                JsonObject metaDataSubtask = validateSubtaskAssignment(subTask, subtaskMetadataMap, messageBadList);
                if (metaDataSubtask != null) {
                    JsonObject maxHistory = validateSubtaskHistory(metaDataSubtask, messageBadList, subtaskLabel);
                    if (maxHistory != null) {
                        String subtaskAssignee = metaDataSubtask.getAsJsonArray(ISSUES)
                                .get(0).getAsJsonObject()
                                .getAsJsonObject(FIELDS)
                                .getAsJsonObject(ASSIGNEE)
                                .get(EMAIL_ADDRESS)
                                .getAsString();

                        validateVoBoAndProjects(teamBackLogId, infoJiraProjectList, subtaskLabel,
                                subtaskAssignee, maxHistory, messageBadList, messageGoodList);
                    }
                }
            }
        }
    }

    private List<String> mergeMessageLists(List<String> goodMessages, List<String> badMessages) {
        List<String> mergedMessages = new ArrayList<>();
        mergedMessages.addAll(goodMessages);
        mergedMessages.addAll(badMessages);
        return mergedMessages;
    }

    private boolean containsAcceptedStatus(JsonArray itemsHistory) {
        if (itemsHistory == null || itemsHistory.isEmpty()) {
            return false;
        }

        for (JsonElement itemElement : itemsHistory) {
            JsonElement itemToString = itemElement.getAsJsonObject().get("toString");
            if (itemToString != null && !itemToString.isJsonNull() &&
                    ACCEPTED.equalsIgnoreCase(itemToString.getAsString())) {
                return true;
            }
        }

        return false;
    }

    private boolean shouldSkipSubtask(String subtaskLabel, JsonElement subTask) {
        if (subtaskLabel.contains("QA") || subtaskLabel.contains("DEV") || subtaskLabel.contains("GC")) {
            return true;
        }
        String subtaskStatus = extractStatus(subTask.getAsJsonObject());
        return DISCARDED.equalsIgnoreCase(subtaskStatus);
    }

    private JsonObject validateSubtaskAssignment(JsonElement subTask, Map<String, JsonObject> subtaskMetadataMap, List<String> messageBadList) {
        String codeJiraSubTask = subTask.getAsJsonObject().get("key").getAsString();
        JsonObject metaData = subtaskMetadataMap.get(codeJiraSubTask);
        if (metaData == null) {
            messageBadList.add(MSG_SUBTAREA + extractLabel(subTask) + " no tiene metadata disponible.");
            return null;
        }
        String subtaskAssignee = metaData.getAsJsonArray(ISSUES)
                .get(0).getAsJsonObject()
                .getAsJsonObject(FIELDS)
                .getAsJsonObject(ASSIGNEE)
                .get(EMAIL_ADDRESS)
                .getAsString();
        if (subtaskAssignee == null || subtaskAssignee.isBlank()) {
            messageBadList.add(MSG_SUBTAREA + extractLabel(subTask) + " sin asignación.");
            return null;
        }
        return metaData;
    }

    private JsonObject validateSubtaskHistory(JsonObject metaData, List<String> messageBadList, String subtaskLabel) {
        LocalDateTime maxDate = null;
        JsonObject maxHistory = null;

        JsonArray historiesSubtask = metaData.getAsJsonArray(ISSUES)
                .get(0).getAsJsonObject()
                .getAsJsonObject(CHANGELOG)
                .getAsJsonArray(HISTORIES);

        for (JsonElement historyElement : historiesSubtask) {
            JsonObject history = historyElement.getAsJsonObject();
            LocalDateTime historyDate = LocalDateTime.parse(history.get(CREATED).getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
            JsonArray itemsHistory = history.getAsJsonArray(ITEMS);

            if (containsAcceptedStatus(itemsHistory) && (maxDate == null || historyDate.isAfter(maxDate))) {
                maxDate = historyDate;
                maxHistory = history;
            }
        }

        if (maxHistory == null) {
            messageBadList.add(MSG_SUBTAREA + subtaskLabel + " no tiene estado Accepted");
        }

        return maxHistory;
    }

    private String extractLabel(JsonElement subTask) {
        return subTask.getAsJsonObject()
                .getAsJsonObject(FIELDS)
                .get(SUMMARY)
                .getAsString();
    }

    private String extractStatus(JsonElement subTask) {
        return subTask.getAsJsonObject()
                .getAsJsonObject(FIELDS)
                .getAsJsonObject(STATUS)
                .get(NAME)
                .getAsString();
    }

    private String extractIssueType(JsonElement subTask) {
        return subTask.getAsJsonObject()
                .getAsJsonObject(FIELDS)
                .getAsJsonObject(ISSUETYPE)
                .get(NAME)
                .getAsString();
        }

    private void validateVoBoAndProjects(
            String teamBackLogId, List<InfoJiraProject> infoJiraProjectList, String subtaskLabel,
            String subtaskAssignee, JsonObject maxHistory, List<String> messageBadList, List<String> messageGoodList) {

        String voboPerson = maxHistory.getAsJsonObject("author").get(EMAIL_ADDRESS).getAsString();

        if (!voboPerson.equalsIgnoreCase(subtaskAssignee)) {
            messageBadList.add(MSG_SUBTAREA + subtaskLabel + " VoBo de " + voboPerson + " no es el mismo asignado en la subtarea");
            return;
        }

        List<InfoJiraProject> filteredProjects = filterProjectsForSubtask(infoJiraProjectList, teamBackLogId, subtaskLabel);
        if (filteredProjects.isEmpty()) {
            messageBadList.add("No se encontró persona para este rol para " + subtaskLabel + " en SIDE");
        } else if (existsVoBoPersonInProjects(filteredProjects, voboPerson)) {
            messageGoodList.add(voboPerson + " para " + subtaskLabel + " es válida");
        }
    }
    private boolean existsVoBoPersonInProjects(List<InfoJiraProject> projects, String voboPerson) {
        return projects.stream()
                .anyMatch(project -> project.getParticipantEmail().equalsIgnoreCase(voboPerson));
    }

    private List<InfoJiraProject> filterProjectsForSubtask(List<InfoJiraProject> infoJiraProjectList, String teamBackLogId, String subtaskLabel) {
        return infoJiraProjectList.stream()
                .filter(project -> {
                    Map<String, Object> subTaskTypeOwner = SUBTASKS_TYPE_OWNER.get(subtaskLabel);
                    if (subTaskTypeOwner != null) {
                        List<String> roles = (List<String>) subTaskTypeOwner.get("rol");
                        boolean validateEmail = (boolean) subTaskTypeOwner.getOrDefault("validateEmailFromLideres", false);
                        return roles.contains(project.getProjectRolType()) &&
                                validateEmail &&
                                project.getTeamBackLogId().equals(teamBackLogId);
                    }
                    return false;
                })
                .toList();
    }

    public Map<String, Object> getValidationValidateSubTaskValidateContractor(Map<String, JsonObject> subtaskMetadataMap, String helpMessage, String group) {
        String message;
        boolean isValid;
        boolean isWarning = false;

        List<String> messageBadList = new ArrayList<>();
        List<String> messageGoodList = new ArrayList<>();

        for (Map.Entry<String, JsonObject> entry : subtaskMetadataMap.entrySet()) {
            JsonObject metaData = entry.getValue();
            validateContractorSubtask(metaData, messageGoodList, messageBadList);
        }

        if (!messageBadList.isEmpty()) {
            messageBadList.addAll(messageGoodList);
            message = String.join(". ", messageBadList);
            isValid = false;
        } else {
            message = String.join(". ", messageGoodList);
            isValid = true;
        }

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    private void validateContractorSubtask(
            JsonObject metaData, List<String> messageGoodList, List<String> messageBadList) {

        String subtaskLabel = extractLabel(metaData.getAsJsonArray(ISSUES).get(0));
        String subtaskStatus = extractStatus(metaData.getAsJsonArray(ISSUES).get(0));

        if (subtaskLabel.contains("QA") || subtaskLabel.contains("DEV") || DISCARDED.equalsIgnoreCase(subtaskStatus)) {
            return;
        }
        JsonObject assignee = metaData.getAsJsonArray(ISSUES)
                .get(0).getAsJsonObject()
                .getAsJsonObject(FIELDS)
                .getAsJsonObject(ASSIGNEE);

        if (assignee == null || assignee.get(EMAIL_ADDRESS).isJsonNull()) {
            messageBadList.add("Subtarea invalida " + subtaskLabel + " no tiene correo asignado.");
        } else {
            String assigneeEmail = assignee.get(EMAIL_ADDRESS).getAsString();
            if (assigneeEmail.contains(".contractor")) {
                messageBadList.add("Subtarea invalida " + subtaskLabel + " asignada a " + assigneeEmail + " no es Interno BBVA.");
            } else {
                messageGoodList.add("Subtarea valida " + subtaskLabel + " asignada a " + assigneeEmail + " es Interno BBVA.");
            }
        }
    }

    public Map<String, Object> getValidationAcceptanceCriteria(
            List<String> teamBackLogTicketIdRLB, String tipoDesarrollo,
            String helpMessage, String group) {
        String message;
        boolean isValid;
        boolean isWarning = false;
        Map<String, Object> validAcceptanceCriteriaObject = CRITERIA_BY_DEVELOP_TYPES.get(tipoDesarrollo);

        String acceptanceCriteria = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .get("customfield_10260").getAsString()
                .replaceAll("[\\s\\u00A0]+", " ")
                .trim();

        if (tipoDesarrollo.equalsIgnoreCase(MALLAS)) {
            String teamBackLogTicketId = jiraTicketResult.getAsJsonObject(FIELDS)
                    .get("customfield_13301").getAsString();
            boolean isTeamBackLogRLB = teamBackLogTicketIdRLB.contains(teamBackLogTicketId);
            if (isTeamBackLogRLB) {
                message = validateAcceptanceCriteriaReliability(acceptanceCriteria, validAcceptanceCriteriaObject);
            } else {
                message = validateAcceptanceCriteriaStandard(acceptanceCriteria, validAcceptanceCriteriaObject);
            }
        } else {
            message = validateAcceptanceCriteriaForOtherTypes(acceptanceCriteria, validAcceptanceCriteriaObject);
        }
        isValid = message.startsWith(MSG_RULE_VALID);

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    private String validateAcceptanceCriteriaReliability(
            String acceptanceCriteria, Map<String, Object> validAcceptanceCriteriaObject) {
        if (acceptanceCriteria.isEmpty()) {
            return MSG_RULE_CRITEROACEPTACION;
        }
        if (validAcceptanceCriteriaObject == null) {
            return MSG_RULE_TIPODESARROLLO;
        }
        String expectedPattern = ((String) validAcceptanceCriteriaObject.get(TEXTO))
                .replace("{0}", "[A-Za-z\\s-/.\\&]+")
                .replaceAll("\\s+", "\\\\s+")
                .replace("\\.", "\\\\.")
                .replace("{1}", "(SDATOOL-\\d{5}|SDATOOL\\s+\\d{5})(.*?)\\s*,") + "?";

        Pattern pattern = Pattern.compile(expectedPattern);
        Matcher matcher = pattern.matcher(acceptanceCriteria);

        if (matcher.matches()) {
            return String.format(MSG_RULE_VALID, acceptanceCriteria);
        }
        return MSG_RULE_CRITERIOFORMATO;
    }

    private String validateAcceptanceCriteriaStandard(
            String acceptanceCriteria, Map<String, Object> validAcceptanceCriteriaObject) {
        if (acceptanceCriteria.isEmpty()) {
            return MSG_RULE_CRITEROACEPTACION;
        }
        if (validAcceptanceCriteriaObject == null) {
            return MSG_RULE_TIPODESARROLLO;
        }
        String expectedPattern = ((String) validAcceptanceCriteriaObject.get(TEXTO))
                .replace("{0}", "[A-Za-z\\s-/.\\&]+")
                .replaceAll("\\s+", "\\\\s+")
                .replace("\\.", "\\\\.")
                .replace("{1}", "(SDATOOL-\\d{5}|SDATOOL\\s+\\d{5})(.*?)\\s*,") + "?";
        Pattern pattern = Pattern.compile(expectedPattern);
        Matcher matcher = pattern.matcher(acceptanceCriteria);

        if (matcher.matches()) {
            return String.format(MSG_RULE_VALID, acceptanceCriteria);
        }
        return MSG_RULE_CRITERIOFORMATO;
    }

    private String validateAcceptanceCriteriaForOtherTypes(
            String acceptanceCriteria, Map<String, Object> validAcceptanceCriteriaObject) {
        if (acceptanceCriteria.isEmpty()) {
            return MSG_RULE_CRITEROACEPTACION;
        }
        if (validAcceptanceCriteriaObject == null) {
            return MSG_RULE_TIPODESARROLLO;
        }
        String expectedPattern = (String) validAcceptanceCriteriaObject.get(TEXTO);
        String[] palabras = expectedPattern.split("\\s+");
        if (palabras.length >= 11) {
            return String.format(MSG_RULE_VALID, acceptanceCriteria);
        }
        return MSG_RULE_CRITERIOFORMATO;
    }


    public Map<String, Object> getValidationTeamAssigned(
            String tipoDesarrollo, boolean validacionEnvioFormulario, String helpMessage, String group) {
        String message;
        boolean isValid;
        boolean isWarning = false;

        String jiraTicketStatus = extractStatus(jiraTicketResult);
        String issueType = extractIssueType(jiraTicketResult);
        String currentTeamFieldLabel = getCurrentTeamFieldLabel(issueType);

        JsonArray histories = jiraTicketResult.getAsJsonObject(CHANGELOG).getAsJsonArray(HISTORIES);
        message = processHistories(histories, currentTeamFieldLabel, tipoDesarrollo, validacionEnvioFormulario, jiraTicketStatus);

        isValid = message.contains("Asignado a Tablero de DQA");

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    private String processHistories(
            JsonArray histories, String currentTeamFieldLabel, String tipoDesarrollo,
            boolean validacionEnvioFormulario, String jiraTicketStatus) {

        List<String> estadosExtraMallasHost = List.of(READY, TEST, READY_TO_VERIFY);
        List<String> statusTableroDQA = Stream.of(
                READY, IN_PROGRESS, TEST, READY_TO_VERIFY, READY_TO_DEPLOY, DEPLOYED, ACCEPTED
        ).map(String::toLowerCase).toList();

        for (JsonElement historyElement : histories) {
            JsonObject history = historyElement.getAsJsonObject();

            if (history.has(ITEMS)) {
                JsonArray items = history.getAsJsonArray(ITEMS);
                for (JsonElement itemElement : items) {
                    JsonObject item = itemElement.getAsJsonObject();

                    if (item.has(FIELD) && item.get(FIELD).getAsString().equals(currentTeamFieldLabel)) {
                        String to = item.get("to").getAsString();
                        if (to.equals(teamBackLogDQAId)) {
                            this.isInTableroDQA = true;
                            return "Asignado a Tablero de DQA";
                        } else if (statusTableroDQA.contains(jiraTicketStatus.trim().toLowerCase())) {
                            return buildWarningMessage(tipoDesarrollo, validacionEnvioFormulario, estadosExtraMallasHost);
                        }
                    }
                }
            }
        }
        return "No está en el Tablero de DQA";
    }

    private String buildWarningMessage(
            String tipoDesarrollo, boolean validacionEnvioFormulario, List<String> estadosExtraMallasHost) {

        StringBuilder sb = new StringBuilder("No está en el Tablero de DQA. ");
        if (validacionEnvioFormulario) {
            sb.append("Atención: No olvidar que para regresar el ticket a DQA, se debe cambiar el estado del ticket y la Subtarea DQA. ");
            if (tipoDesarrollo.equalsIgnoreCase(MALLAS) || tipoDesarrollo.equalsIgnoreCase("HOST")) {
                sb.append(String.join(", ", estadosExtraMallasHost));
            } else {
                sb.append(READY);
            }
        }
        return sb.toString();
    }

    private String getCurrentTeamFieldLabel(String issueType) {
        Map<String, String> storyMap = Map.of(
                LABEL, TEAM_BACKLOG,
                FIELD, "teamId"
        );

        Map<String, Map<String, String>> teamFieldLabelByIssueType = Map.of(
                "Historia", storyMap,
                STORY, storyMap,
                DEPENDENCY, storyMap
        );

        return teamFieldLabelByIssueType.getOrDefault(issueType, Map.of()).getOrDefault(LABEL, "");
    }

    public Map<String, Object> getValidationValidateJIRAStatus(String tipoDesarrollo, String helpMessage, String group) {
        String message;
        boolean isValid = false;
        boolean isWarning = false;

        var jiraTicketStatus = extractStatus(jiraTicketResult);
        List<String> estadosExtraMallasHost = new ArrayList<>(Arrays.asList(READY, TEST, READY_TO_VERIFY));
        List<String> statusTableroDQA = new ArrayList<>(Arrays.asList(
                READY,
                IN_PROGRESS,
                TEST,
                READY_TO_VERIFY,
                READY_TO_DEPLOY,
                DEPLOYED,
                ACCEPTED
        ));

        if (tipoDesarrollo.equals(MALLAS) || tipoDesarrollo.equals("HOST")) {
            statusTableroDQA.addAll(estadosExtraMallasHost);
        }

        message = String.format("Con estado %s", jiraTicketStatus);

        if (statusTableroDQA.contains(jiraTicketStatus)) {
            isValid = true;

            List<String> listaEstados = new ArrayList<>(Arrays.asList(READY, DEPLOYED));

            if (!listaEstados.contains(jiraTicketStatus)) {
                if (this.isInTableroDQA) {
                    isWarning = true;
                    message += " Atención: Es posible que el %s se encuentre en revisión, recordar que el estado inicial de un %s por revisar es Ready";
                } else {
                    isValid = false;
                }
            }
        }
        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationFeatureLink(String helpMessage, String group) {
        String message;
        boolean isValid;
        boolean isWarning = false;

        if (featureLinkCode == null || featureLinkCode.isBlank()) {
            message = MSG_RULE_NOFEATURE;
            isValid = false;
        } else {
            message = ApiJiraName.URL_API_BROWSE + featureLinkCode + " asociado correctamente";
            isValid = true;
        }
        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }


    public Map<String, Object> getValidationFeatureLinkStatus(String helpMessage, String group) {
        String message;
        boolean isValid = false;
        boolean isWarning = false;
        List<String> validStatus = List.of(IN_PROGRESS);
        Map<String, Object> validationFeatureResult = getValidationFeatureLink(helpMessage, group);
        if (!(boolean) validationFeatureResult.get(ISVALID)) {
            return validationFeatureResult;
        }
        String featureLinkStatus = extractStatus(featureLinkResult
                .getAsJsonArray(ISSUES)
                .get(0));

        if (validStatus.contains(featureLinkStatus)) {
            isValid = true;
        }
        message = "Con estado " + featureLinkStatus;

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationFeatureLinkProgramIncrement(String helpMessage, String group) {
        String message;
        boolean isValid;
        boolean isWarning = false;
        Map<String, Object> validationFeatureResult = getValidationFeatureLink(helpMessage, group);
        if (!(boolean) validationFeatureResult.get(ISVALID)) {
            return validationFeatureResult;
        }

        JsonArray programIncrement = extractProgramIncrement(featureLinkResult);

        if (programIncrement == null || programIncrement.isEmpty()) {
            message = handleMissingProgramIncrement();
            isValid = message.contains("tipo de incidencia");
            isWarning = isValid;
        } else {
            message = handleProgramIncrementValidation(programIncrement, extractStatus(jiraTicketResult));
            isValid = !message.contains("Atención");
        }

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    private JsonArray extractProgramIncrement(JsonObject featureLinkResult) {
        if (featureLinkResult.has(ISSUES) &&
                !featureLinkResult.getAsJsonArray(ISSUES).isEmpty() &&
                featureLinkResult.getAsJsonArray(ISSUES)
                        .get(0).getAsJsonObject().getAsJsonObject(FIELDS)
                        .has(CUSTOMFIELD_10264)) {
            return featureLinkResult
                    .getAsJsonArray(ISSUES)
                    .get(0).getAsJsonObject()
                    .getAsJsonObject(FIELDS)
                    .get(CUSTOMFIELD_10264)
                    .getAsJsonArray();
        }
        return null;
    }

    private String handleMissingProgramIncrement() {
        String tipoIncidencia = extractIssueType(jiraTicketResult);
        if (!tipoIncidencia.isEmpty()) {
            return "Sin Program Increment, pero con tipo de incidencia: " + tipoIncidencia;
        }
        return "Sin Program Increment";
    }

    private String handleProgramIncrementValidation(JsonArray programIncrement, String jiraTicketStatus) {
        StringBuilder message = new StringBuilder("Con Program Increment " + programIncrement);
        boolean containsCurrentQ = programIncrementContainsCurrentQ(programIncrement);

        if (!jiraTicketStatus.equals(DEPLOYED) && !containsCurrentQ) {
            message.append(" Atención: El Program Increment debe contener al Q actual (En este caso ")
                    .append(currentQ).append(") cuando el ticket este en revisión, ")
                    .append(MSG_COORDINATION_MESSAGE);
        }

        return message.toString();
    }

    private boolean programIncrementContainsCurrentQ(JsonArray programIncrement) {
        for (JsonElement element : programIncrement) {
            if (element.getAsString().equals(currentQ)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, Object> getValidationFeatureLinkRLB(
            String teamBackLogId, List<String> teamBackLogTicketIdRLB, String helpMessage, String group) {
        String message;
        boolean isValid;
        boolean isWarning = false;
        List<String> validStatus = List.of("INC", "PRB", "PB");

        Map<String, Object> validationFeatureResult = getValidationFeatureLink(helpMessage, group);
        if (!(boolean) validationFeatureResult.get(ISVALID)) {
            return validationFeatureResult;
        }

        if (!teamBackLogTicketIdRLB.contains(teamBackLogId)) {
            return buildValidationResult(MSG_RULE_INVALID, true, isWarning, helpMessage, group);
        }

        JsonArray featureLinkLabels = extractFeatureLinkLabels(featureLinkResult);

        if (featureLinkLabels == null || featureLinkLabels.isEmpty()) {
            message = "El ticket no es una incidencia o problema o no contiene los labels identificadores "
                    + String.join(",", validStatus) + ".";
            isValid = true;
        } else {
            boolean containsValidStatus = checkLabels(featureLinkLabels, validStatus);
            if (containsValidStatus) {
                message = "El ticket es una incidencia o problema, contiene los labels correspondientes.";
                isValid = true;
            } else {
                message = "El ticket debe corresponder a un evolutivo o solicitud interna para no llevar los labels correspondientes.";
                isValid = true;
                isWarning = true;
            }
        }

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    private JsonArray extractFeatureLinkLabels(JsonObject featureLinkResult) {
        if (featureLinkResult.has(ISSUES) &&
                !featureLinkResult.getAsJsonArray(ISSUES).isEmpty() &&
                featureLinkResult.getAsJsonArray(ISSUES)
                        .get(0).getAsJsonObject().getAsJsonObject(FIELDS)
                        .has(LABELS)) {
            return featureLinkResult
                    .getAsJsonArray(ISSUES)
                    .get(0).getAsJsonObject()
                    .getAsJsonObject(FIELDS)
                    .getAsJsonArray(LABELS);
        }
        return null;
    }

    private boolean checkLabels(JsonArray featureLinkLabels, List<String> validStatus) {
        for (JsonElement labelElement : featureLinkLabels) {
            String label = labelElement.getAsString();
            for (String status : validStatus) {
                if (label.startsWith(status)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Map<String, Object> getValidationIFRS9(String helpMessage, String group) {
        String message;
        boolean isValid = true;
        boolean isWarning = false;

        LocalDate today = LocalDate.now();

        LocalDate firstDayOfNextMonth = today.with(TemporalAdjusters.firstDayOfNextMonth());

        LocalDate lastBusinessDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        while (lastBusinessDayOfMonth.getDayOfWeek() == DayOfWeek.SATURDAY || lastBusinessDayOfMonth.getDayOfWeek() == DayOfWeek.SUNDAY) {
            lastBusinessDayOfMonth = lastBusinessDayOfMonth.minusDays(1);
        }

        LocalDate secondLastBusinessDayOfMonth = lastBusinessDayOfMonth.minusDays(1);
        while (secondLastBusinessDayOfMonth.getDayOfWeek() == DayOfWeek.SATURDAY || secondLastBusinessDayOfMonth.getDayOfWeek() == DayOfWeek.SUNDAY) {
            secondLastBusinessDayOfMonth = secondLastBusinessDayOfMonth.minusDays(1);
        }

        LocalDate currentDate = firstDayOfNextMonth;
        int businessDaysCount = 0;

        while (businessDaysCount < 4) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                businessDaysCount++;
            }
            if (businessDaysCount < 4) {
                currentDate = currentDate.plusDays(1);
            }
        }

        LocalDate fourthBusinessDay = currentDate.minusDays(1);

        boolean isWarningDate = (today.equals(secondLastBusinessDayOfMonth) || today.equals(lastBusinessDayOfMonth))
                || (today.isAfter(firstDayOfNextMonth.minusDays(1)) && today.isBefore(fourthBusinessDay.plusDays(1)));

        if (isWarningDate) {
            message = "La validación se está realizando durante el período de bloqueos. Revisar la lista de bloqueos IFRS9.";
            isWarning = true;
        } else {
            message = "No se encontraron advertencias relacionadas con la fecha.";
        }

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationValidateImpactLabel(
            String helpMessage, String group, String tipoDesarrollo) {

        String message;
        boolean isValid = false;
        boolean isWarning = false;

        if (tipoDesarrollo.equalsIgnoreCase(HOST) || tipoDesarrollo.equalsIgnoreCase(MALLAS)) {
            JsonObject fieldsObject = jiraTicketResult.getAsJsonObject(FIELDS);

            List<String> jiraTicketImpactLabelList = extractImpactLabels(fieldsObject);
            if (jiraTicketImpactLabelList.isEmpty()) {
                message = "No se tiene Impact Label definidos";
                return buildValidationResult(message, isValid, isWarning, helpMessage, group);
            }

            List<String> validImpactLabels = tipoDesarrollo.equalsIgnoreCase("HOST")
                    ? List.of("DataHub", "Host", "Plataforma_InformacionalP11")
                    : List.of("AppsInternos", "Datio");

            List<String> impactLabelsNotFound = getMissingLabels(validImpactLabels, jiraTicketImpactLabelList);

            message = generateImpactLabelMessage(validImpactLabels, jiraTicketImpactLabelList, impactLabelsNotFound);
            isValid = impactLabelsNotFound.isEmpty();
        } else {
            message = MSG_RULE_INVALID;
            isValid = true;
        }

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }


    public Map<String, Object> getValidationFixVersion(String tipoDesarrollo, String helpMessage, String group) {
        String message;
        boolean isValid;
        boolean isWarning = false;
        if (tipoDesarrollo.equals("HOST") || tipoDesarrollo.equals(MALLAS)) {
            String[] jiraCodeParts = this.jiraCode.split("-");
            String jiraPADCode = jiraCodeParts[0].toUpperCase();

            JsonArray fixVersions = this.jiraTicketResult.getAsJsonObject(FIELDS).getAsJsonArray("fixVersions");

            if (!fixVersions.isEmpty()) {
                String fixVersionURLPrefix = ApiJiraName.URL_API_BASE + "/issues?jql=project%20%3D%20" + jiraPADCode + "%20AND%20fixVersion%20%3D%20";

                List<String> fixVersionsUrlLinkList = new ArrayList<>();
                for (JsonElement fixVersion : fixVersions) {
                    String fixVersionName = fixVersion.getAsJsonObject().get(NAME).getAsString();
                    String fixVersionUrl = fixVersionURLPrefix + fixVersionName;
                    fixVersionsUrlLinkList.add(fixVersionUrl);
                }

                message = "Con Fix Version " + fixVersionsUrlLinkList;
                isValid = true;
            } else {
                message = "Sin Fix Version asignado";
                isValid = false;
            }
        }
        else{
            message = MSG_RULE_INVALID;
            isValid = true;
        }

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationValidateAttachment(String tipoDesarrollo, String helpMessage, String group) {
        String message;
        boolean isValid;
        boolean isWarning = false;
        List<String> requiredAttachments = ATTACHS_BY_DEVELOP_TYPES.get(tipoDesarrollo.toLowerCase());
        JsonArray attachments = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonArray(ATTACHMENT);
        List<String> foundAttachments = new ArrayList<>();

        if(tipoDesarrollo.equals("productivizacion")){
            message = "Esta regla no es válida para este tipo de desarrollo";
            isValid = true;
            return buildValidationResult(message, isValid, isWarning, helpMessage, group);
        }

        for (JsonElement attachment : attachments) {
            String filename = attachment.getAsJsonObject().get("filename").getAsString();
            String attachmentLabel = filename.split("-")[0].replaceAll("\\s+", "");
            if (requiredAttachments.contains(attachmentLabel)) {
                foundAttachments.add(attachmentLabel);
            }
        }

        if (new HashSet<>(foundAttachments).containsAll(requiredAttachments)) {
            message = "Todos los adjuntos requeridos fueron encontrados: " + String.join(", ", requiredAttachments);
            isValid = true;
        } else {
            List<String> missingAttachments = new ArrayList<>(requiredAttachments);
            missingAttachments.removeAll(foundAttachments);
            message = "Faltan los siguientes adjuntos: " + String.join(", ", missingAttachments);
            isValid = false;
        }
        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    private List<String> extractImpactLabels(JsonObject fieldsObject) {
        List<String> impactLabels = new ArrayList<>();
        if (fieldsObject.has("customfield_10267")) {
            JsonElement impactLabelElement = fieldsObject.get("customfield_10267");
            if (impactLabelElement != null && impactLabelElement.isJsonArray()) {
                JsonArray impactLabelArray = impactLabelElement.getAsJsonArray();
                for (JsonElement element : impactLabelArray) {
                    impactLabels.add(element.getAsString());
                }
            }
        }
        return impactLabels;
    }

    private List<String> getMissingLabels(List<String> validImpactLabels, List<String> jiraTicketImpactLabelList) {
        List<String> impactLabelsNotFound = new ArrayList<>();
        for (String label : validImpactLabels) {
            if (!jiraTicketImpactLabelList.contains(label)) {
                impactLabelsNotFound.add(label);
            }
        }
        return impactLabelsNotFound;
    }

    private String generateImpactLabelMessage(
            List<String> validImpactLabels, List<String> jiraTicketImpactLabelList, List<String> impactLabelsNotFound) {
        if (impactLabelsNotFound.isEmpty()) {
            return "Todos los Impact Label requeridos fueron encontrados: " + String.join(", ", validImpactLabels);
        }
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Falta Impact Label: ").append(String.join(", ", impactLabelsNotFound));
        if (!jiraTicketImpactLabelList.isEmpty()) {
            messageBuilder.append(" Se tiene: ").append(String.join(", ", jiraTicketImpactLabelList));
        } else {
            messageBuilder.append(" No se tiene Impact Label definidos");
        }
        return messageBuilder.toString();
    }

    public Map<String, Object> getValidationProductivizacionIssueLink(
            String tipoDesarrollo, String helpMessage, String group) {
        String message;
        boolean isValid = false;
        boolean isWarning = false;

        if (!tipoDesarrollo.equalsIgnoreCase("productivizacion")) {
            isValid = true;
            message = MSG_RULE_INVALID;
            return buildValidationResult(message, isValid, isWarning, helpMessage, group);
        }

        JsonArray issueLinkStory = extractStoriesFromIssueLinks(
                jiraTicketResult.getAsJsonObject(FIELDS).getAsJsonArray(ISSUELINKS)
        );

        if (issueLinkStory.isEmpty()) {
            message = "No se encontraron tickets asociados de tipo Story";
        } else {
            message = validateStoryStatus(issueLinkStory);
            isValid = !message.contains("No todos los tickets");
        }

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    private JsonArray extractStoriesFromIssueLinks(JsonArray issuelinks) {
        JsonArray issueLinkStory = new JsonArray();
        for (JsonElement issueLinkElement : issuelinks) {
            JsonObject issueLinkObject = issueLinkElement.getAsJsonObject();
            String type = issueLinkObject.getAsJsonObject(TYPE).get(INWARD).getAsString();
            JsonElement inwardIssue = issueLinkObject.getAsJsonObject(INWARD_ISSUE);

            if (type.equals(IS_CHILD_ITEM_OF) && inwardIssue != null) {
                String name = extractIssueType(inwardIssue);
                if (name.equals(STORY)) {
                    issueLinkStory.add(issueLinkElement);
                }
            }
        }

        return issueLinkStory;
    }

    private String validateStoryStatus(JsonArray issueLinkStory) {
        for (JsonElement issueLinkElement : issueLinkStory) {
            String statusCategory = extractStatus(
                    issueLinkElement.getAsJsonObject().getAsJsonObject(INWARD_ISSUE)
            ).toLowerCase();

            if (!statusCategory.equalsIgnoreCase(DEPLOYED)) {
                return "No todos los tickets asociados se encuentran deployados";
            }
        }
        return "Todos los tickets asociados se encuentran deployados";
    }

    public Map<String, Object> getValidationLabels(String tipoDesarrollo, String helpMessage, String group){
        String message;
        boolean isValid;
        boolean isWarning = false;
        List<String> requiredLabels = LABELS_BY_DEVELOP_TYPES.get(tipoDesarrollo.toLowerCase());
        JsonArray labels = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonArray(LABELS);
        List<String> foundLabels = new ArrayList<>();

        for (JsonElement label : labels) {
            String labelName = label.getAsString();
            if (requiredLabels.contains(labelName)) {
                foundLabels.add(labelName);
            }
        }

        if (new HashSet<>(foundLabels).containsAll(requiredLabels)) {
            message = "Todas las etiquetas correspondientes fueron encontradas: "+ String.join(", ", requiredLabels);
            isValid = true;
        } else {
            List<String> missingLabels = new ArrayList<>(requiredLabels);
            missingLabels.removeAll(foundLabels);
            message = "Faltan las siguientes etiquetas: " + String.join(", ", missingLabels);
            isValid = false;
        }
        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationInitialTeam(String helpMessage, String group) throws ParseException {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        JsonArray changelog = jiraTicketResult.getAsJsonObject(CHANGELOG).getAsJsonArray(HISTORIES);
        Date oldestDate = new SimpleDateFormat("yyyy-MM-dd").parse("9999-12-31");
        String extractedContent;

        for (JsonElement history : changelog) {
            JsonObject historyObj = history.getAsJsonObject();
            Date createdDate = extractCreatedDate(historyObj);

            if (createdDate.before(oldestDate)) {
                JsonArray items = historyObj.getAsJsonArray(ITEMS);
                if (isTeamBacklogField(items)) {
                    String from = extractFieldValue(items, "from");
                    String fromString = extractFieldValue(items, "fromString");
                    extractedContent = extractContentFrom(fromString);

                    if (from.equals(teamBackLogDQAId)) {
                        isValid = false;
                        message = "Se creó en tablero DQA.";
                    } else {
                        isValid = true;
                        message = "Se creó en tablero diferente a DQA: " + extractedContent + ".";
                    }
                    oldestDate = createdDate;
                }
            }
        }

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    private Date extractCreatedDate(JsonObject historyObj) throws ParseException {
        String created = historyObj.get(CREATED).getAsString();
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(created);
    }

    private boolean isTeamBacklogField(JsonArray items) {
        String field = items.get(0).getAsJsonObject().get(FIELD).getAsString();
        return TEAM_BACKLOG.equals(field);
    }

    private String extractFieldValue(JsonArray items, String fieldKey) {
        JsonObject item = items.get(0).getAsJsonObject();
        return item.has(fieldKey) && !item.get(fieldKey).isJsonNull()
                ? item.get(fieldKey).getAsString()
                : "";
    }

    private String extractContentFrom(String fromString) {
        Pattern pattern = Pattern.compile("<span style=\"color: #fff\">(.*?)</span>");
        Matcher matcher = pattern.matcher(fromString);
        return matcher.find() ? matcher.group(1) : "";
    }

    public Map<String, Object> getValidationDependency(
            String teamBackLogId, List<String> teamBackLogTicketIdRLB, String helpMessage, String group) {
        String message;
        boolean isValid = false;
        boolean isWarning = false;

        if (teamBackLogTicketIdRLB.contains(teamBackLogId)) {
            message = "Proviene del tablero RLB, por lo tanto, no tiene una dependencia asociada.";
            isValid = true;
            return buildValidationResult(message, isValid, isWarning, helpMessage, group);
        }
        JsonArray issueLinks = jiraTicketResult.getAsJsonObject(FIELDS).getAsJsonArray(ISSUELINKS);
        if (issueLinks == null || issueLinks.isEmpty()) {
            message = MSG_RULE_NODEPENDENCY;
            return buildValidationResult(message, isValid, isWarning, helpMessage, group);
        }

        List<String> statusDependencyCollection = new ArrayList<>();
        List<String> dependencyPadCollection = new ArrayList<>();
        extractDependencyData(issueLinks, statusDependencyCollection, dependencyPadCollection);

        if (statusDependencyCollection.isEmpty()) {
            message = MSG_RULE_NODEPENDENCY;
            return buildValidationResult(message, isValid, isWarning, helpMessage, group);
        }
        if (areAllDependenciesInProgress(statusDependencyCollection)) {
            isValid = true;
            message = "Todas las dependencias se encuentran en el estado que corresponde: " + IN_PROGRESS;
        } else {
            isWarning = true;
            message = buildWarningMessage(statusDependencyCollection, dependencyPadCollection);
        }

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    private void extractDependencyData(
            JsonArray issueLinks, List<String> statusDependencyCollection, List<String> dependencyPadCollection) {
        for (JsonElement issueLinkElement : issueLinks) {
            JsonObject issueLinkObject = issueLinkElement.getAsJsonObject();
            String type = issueLinkObject.getAsJsonObject(TYPE).get(INWARD).getAsString();
            JsonElement inwardIssue = issueLinkObject.getAsJsonObject(INWARD_ISSUE);

            if (type.equals(IS_CHILD_ITEM_OF) && inwardIssue != null) {
                String issueType = extractIssueType(inwardIssue);
                if (issueType.equalsIgnoreCase(DEPENDENCY)) {
                    String statusDependencyTicket = extractStatus(inwardIssue.getAsJsonObject()).toLowerCase();
                    statusDependencyCollection.add(statusDependencyTicket);

                    String dependencyKey = inwardIssue.getAsJsonObject().get("key").getAsString();
                    dependencyPadCollection.add(dependencyKey);
                }
            }
        }
    }

    private boolean areAllDependenciesInProgress(List<String> statusDependencyCollection) {
        return statusDependencyCollection.stream()
                .allMatch(status -> status.equalsIgnoreCase(IN_PROGRESS));
    }

    private String buildWarningMessage(List<String> statusDependencyCollection, List<String> dependencyPadCollection) {
        StringBuilder urlMessage = new StringBuilder("Las siguientes dependencias: ");
        for (int i = 0; i < statusDependencyCollection.size(); i++) {
            if (!statusDependencyCollection.get(i).equalsIgnoreCase(IN_PROGRESS)) {
                urlMessage.append(ApiJiraName.URL_API_BROWSE)
                        .append(dependencyPadCollection.get(i))
                        .append(", ");
            }
        }
        urlMessage.setLength(urlMessage.length() - 2);
        urlMessage.append(" no se encuentran en el estado correspondiente (In Progress).");
        return urlMessage.toString();
    }

    public Map<String, Object> getValidationDependencyFeatureVsHUTFeature(
            String teamBackLogId, List<String> teamBackLogTicketIdRLB, JiraValidatorByUrlRequest dto,
            String helpMessage, String group) {
        boolean isValid = true;
        boolean isWarning = false;
        String message = "Todas las dependencias tienen el mismo feature link";

        if (teamBackLogTicketIdRLB.contains(teamBackLogId)) {
            message = "Proviene del tabero RLB, por lo que no tiene dependencia asociada y, en consecuencia, esta regla no es aplicable.";
            return buildValidationResult(message, isValid, isWarning, helpMessage, group);
        }

        JsonArray issueLinks = jiraTicketResult.getAsJsonObject(FIELDS).getAsJsonArray(ISSUELINKS);
        if (issueLinks == null || issueLinks.isEmpty()) {
            isValid = false;
            message = MSG_RULE_NODEPENDENCY;
            return buildValidationResult(message, isValid, isWarning, helpMessage, group);
        }

        List<String> isChildPadNameCollection = extractDependencyKeys(issueLinks);

        try {
            if (!validateFeatureLinks(isChildPadNameCollection, dto)) {
                isValid = false;
                message = "No todas las dependencias tienen el mismo features link";
            }
        } catch (Exception e) {
            LOGGER.info("ERROR CONSULTA DEPENDENCIA LINK: " + e.getMessage());
        }

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    private List<String> extractDependencyKeys(JsonArray issueLinks) {
        List<String> isChildPadNameCollection = new ArrayList<>();

        for (JsonElement issueLinkElement : issueLinks) {
            JsonObject issueLinkObject = issueLinkElement.getAsJsonObject();
            String type = issueLinkObject.getAsJsonObject(TYPE).get(INWARD).getAsString();
            JsonElement inwardIssue = issueLinkObject.getAsJsonObject(INWARD_ISSUE);

            if (type.equals(IS_CHILD_ITEM_OF) && inwardIssue != null) {
                String issueType = extractIssueType(inwardIssue);
                if (issueType.equalsIgnoreCase(DEPENDENCY)) {
                    String isChildPadName = inwardIssue.getAsJsonObject().get("key").getAsString();
                    isChildPadNameCollection.add(isChildPadName);
                }
            }
        }

        return isChildPadNameCollection;
    }

    private boolean validateFeatureLinks(List<String> isChildPadNameCollection, JiraValidatorByUrlRequest dto) throws Exception {
        for (String isChildPad : isChildPadNameCollection) {
            var query = KEY_IN + isChildPad + ")";
            var url = ApiJiraName.URL_API_JIRA_SQL + query + new JiraApiService().getQuerySuffixURL();
            var response = new JiraApiService().GetJiraAsync(dto.getUserName(), dto.getToken(), url);
            JsonObject metaData = JsonParser.parseString(response).getAsJsonObject();

            String isChildFeatureLink = metaData
                    .getAsJsonArray(ISSUES)
                    .get(0).getAsJsonObject()
                    .getAsJsonObject(FIELDS)
                    .get(CUSTOMFIELD_10004).getAsString();

            if (!isChildFeatureLink.equals(featureLinkCode)) {
                return false;
            }
        }
        return true;
    }

    public Map<String, Object> getValidationBoardProject(String teamBackLogId,String helpMessage, String group,List<InfoJiraProject> infoJiraProjectList) {
        String message = "El tablero es valido";
        boolean isValid = false;
        boolean isWarning = false;
        String summaryTicket = extractLabel(jiraTicketResult.getAsJsonObject());
        String teamBackLogFeatureId = "";

        Map<String, Object> validationFeatureResult = getValidationFeatureLink(helpMessage, group);
        if (!(boolean) validationFeatureResult.get(ISVALID)) {
            return validationFeatureResult;
        }

        if (featureLinkResult.has(ISSUES) &&
                !featureLinkResult.getAsJsonArray(ISSUES).isEmpty() &&
                featureLinkResult.getAsJsonArray(ISSUES)
                        .get(0).getAsJsonObject().getAsJsonObject(FIELDS)
                        .has(CUSTOMFIELD_13300)) {

            teamBackLogFeatureId = featureLinkResult
                    .getAsJsonArray(ISSUES)
                    .get(0).getAsJsonObject()
                    .getAsJsonObject(FIELDS)
                    .getAsJsonArray(CUSTOMFIELD_13300).get(0).getAsString();
        }

        if (!teamBackLogFeatureId.equals(teamBackLogId)){
            message = "El tablero del Ticket es distinto al tablero del Feature";
            return buildValidationResult(message, false, false, helpMessage, group);
        }

        List<InfoJiraProject> projectFiltrado = infoJiraProjectList.stream().filter(project -> project.getTeamBackLogId() != null
                && project.getTeamBackLogId().equals(teamBackLogId)).toList();

        if (!projectFiltrado.isEmpty()) {
            String tableroNombre = projectFiltrado.get(0).getTeamBackLogName().trim();
            if (!summaryTicket.toLowerCase().contains(tableroNombre.toLowerCase())) {
                message="El tablero del Ticket es distinto al mencionado en el summary";
                isWarning = true;
            }
        }
        else {
            message = "El tablero no se ha encontrado como válido para los proyectos habilitados del Q";
        }
        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationAlpha(String tipoDesarrollo, String helpMessage, String group) {
        String message;
        boolean isValid = false;
        boolean isWarning = false;

        String alphaVoBo = "[VB][ALPHA]";
        List<String> alphaUuaas = List.of("KLIM", "KFUL", "ATAU", "KSKR", "KMOL", "KAGE", "KSAN", "W1BD", "KCOL");

        if (!tipoDesarrollo.equalsIgnoreCase(MALLAS)) {
            message = MSG_RULE_INVALID;
            isValid = true;
            return buildValidationResult(message, isValid, isWarning, helpMessage, group);
        }
        List<String> attachmentFilenameList = extractAttachmentFilenames(jiraTicketResult);
        if (attachmentFilenameList.isEmpty()) {
            message = "No se pudo Validar Alpha por no tener adjuntos.";
            return buildValidationResult(message, isValid, isWarning, helpMessage, group);
        }
        List<String> matchedUuaas = findMatchedUuaas(attachmentFilenameList, alphaUuaas);
        if (matchedUuaas.isEmpty()) {
            message = "No se encontró UUAA bajo dominio de Alpha.";
            isValid = true;
            return buildValidationResult(message, isValid, isWarning, helpMessage, group);
        }
        message = validateAlphaSubtasks(matchedUuaas, jiraTicketResult, alphaVoBo);
        isValid = message.contains("estado Accepted");
        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    private List<String> extractAttachmentFilenames(JsonObject jiraTicketResult) {
        List<String> attachmentFilenameList = new ArrayList<>();
        JsonArray attachments = jiraTicketResult.getAsJsonObject(FIELDS).getAsJsonArray(ATTACHMENT);

        for (JsonElement attachment : attachments) {
            String filename = attachment.getAsJsonObject().get("filename").getAsString();
            attachmentFilenameList.add(filename);
        }
        return attachmentFilenameList;
    }

    private List<String> findMatchedUuaas(List<String> attachmentFilenameList, List<String> alphaUuaas) {
        return alphaUuaas.stream()
                .filter(uuaa -> attachmentFilenameList.stream().anyMatch(fileName -> fileName.contains(uuaa)))
                .toList();
    }

    private String validateAlphaSubtasks(List<String> matchedUuaas, JsonObject jiraTicketResult, String alphaVoBo) {
        JsonArray subTasks = jiraTicketResult.getAsJsonObject(FIELDS).getAsJsonArray(SUBTASKS);
        for (JsonElement subTask : subTasks) {
            String subTaskLabel = extractLabel(subTask.getAsJsonObject());
            if (subTaskLabel.equals(alphaVoBo)) {
                String statusSubtask = extractStatus(subTask.getAsJsonObject());
                if (statusSubtask.equals(ACCEPTED)) {
                    return MSG_UUAA + String.join(", ", matchedUuaas) +
                            " bajo dominio de Alpha y Subtarea en estado Accepted.";
                } else {
                    return MSG_UUAA + String.join(", ", matchedUuaas) +
                            " bajo dominio de Alpha y Subtarea en estado incorrecto " + statusSubtask + ".";
                }
            }
        }
        return MSG_UUAA + String.join(", ", matchedUuaas) + " bajo dominio de Alpha sin Subtarea.";
    }

    public Map<String, Object> getValidationItemType(String helpMessage, String group) {
        String message;
        boolean isValid = false;
        boolean isWarning = false;
        JsonObject fields = jiraTicketResult
                .getAsJsonObject(FIELDS);
        if (fields.has("customfield_10270")){
            String itemType = fields.getAsJsonObject("customfield_10270").get("value").getAsString();
            if(itemType.equals("Technical")){
                message = "Se encontró Item Type correcto Technical";
                isValid = true;
            }
            else{
                message = "Valor de Item Type no es el correcto";
            }
        }
        else{
            message = "No se encontró Item Type";
        }

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationTechStack(String helpMessage, String group) {
        String message;
        boolean isValid = false;
        boolean isWarning = false;

        JsonObject fields = jiraTicketResult.getAsJsonObject(FIELDS);
        if (fields.has("customfield_18001")){
            String itemType = fields.getAsJsonObject("customfield_18001").get("value").getAsString();
            if(itemType.equals("Data - Dataproc") || itemType.equals("DATIO (Dataproc)")){
                message = "Se encontró Tech Stack correcto Data - Dataproc";
                isValid = true;
            }
            else{
                message = "Valor de Tech Stack no es el correcto";
            }
        }
        else{
            message = "No se encontró Item Type";
        }
        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationDependencyComment(
            String teamBackLogId, List<String> teamBackLogTicketIdRLB, JiraValidatorByUrlRequest dto,
            String helpMessage, String group, List<InfoJiraProject> infoJiraProjectList) {
        boolean isValid = false;
        boolean isWarning = false;
        String message = "";

        JsonArray issueLinks = jiraTicketResult.getAsJsonObject(FIELDS).getAsJsonArray(ISSUELINKS);
        if (issueLinks == null || issueLinks.isEmpty()) {
            return buildValidationResult(MSG_RULE_NODEPENDENCY, false, isWarning, helpMessage, group);
        }
        if (teamBackLogTicketIdRLB.contains(teamBackLogId)) {
            return buildValidationResult("Esta regla no es válida para RLB.", true, isWarning, helpMessage, group);
        }
        List<String> isChildPadNameCollection = extractDependencyKeys(issueLinks);
        if (isChildPadNameCollection.isEmpty()) {
            return buildValidationResult(
                    "Ticket no cuenta con Dependencia Asociada de Type \"Dependency\" o su asociación no es \"is child item of\".",
                    false, isWarning, helpMessage, group
            );
        }
        try {
            message = validateDependencyComments(isChildPadNameCollection, dto, teamBackLogId, infoJiraProjectList);
            isValid = !message.contains("no cuenta con");
            isWarning = message.contains("pero no está asociado");
        } catch (Exception e) {
            LOGGER.info("ERROR CONSULTA DEPENDENCIA LINK: " + e.getMessage());
        }

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    private String validateDependencyComments(
            List<String> isChildPadNameCollection, JiraValidatorByUrlRequest dto,
            String teamBackLogId, List<InfoJiraProject> infoJiraProjectList) throws Exception {
        List<String> rolIdQE = List.of("11", "12"); // QE y QE Temporal
        for (String isChildPad : isChildPadNameCollection) {
            JsonArray comments = fetchDependencyComments(isChildPad, dto);
            if (comments.isEmpty()) {
                return "Dependencia Asociada no tiene comentarios.";
            }
            for (JsonElement comment : comments) {
                String authorEmailAddress = comment.getAsJsonObject().getAsJsonObject("author").get(EMAIL_ADDRESS).getAsString();
                String comentario = comment.getAsJsonObject().get("body").getAsString();
                if (containsComprometido(comentario)) {
                    return evaluateCommentAuthor(authorEmailAddress, teamBackLogId, infoJiraProjectList, rolIdQE);
                }
            }
        }
        return "Dependencia no cuenta con ningún comentario \"Comprometido\".";
    }

    private boolean containsComprometido(String comentario) {
        String patron = "comprometid";
        Pattern pattern = Pattern.compile(patron, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(comentario);
        return matcher.find();
    }

    private String evaluateCommentAuthor(
            String authorEmailAddress, String teamBackLogId,
            List<InfoJiraProject> infoJiraProjectList, List<String> rolIdQE) {

        List<InfoJiraProject> filteredProjects = infoJiraProjectList.stream()
                .filter(project -> project.getTeamBackLogId().equals(teamBackLogId)
                        && project.getParticipantEmail().equals(authorEmailAddress)
                        && rolIdQE.contains(project.getProjectRolType()))
                .toList();

        List<String> personsRolIdQE = infoJiraProjectList.stream()
                .filter(project -> rolIdQE.contains(project.getProjectRolType()))
                .map(InfoJiraProject::getParticipantEmail)
                .distinct()
                .toList();

        if (filteredProjects.isEmpty()) {
            if (personsRolIdQE.contains(authorEmailAddress)) {
                return "Dependencia cuenta con comentario \"Comprometido\", QE o del QE temporal " + authorEmailAddress
                        + " pero no está asociado al proyecto.";
            } else {
                return "Dependencia cuenta con comentario \"Comprometido\", pero no de algún QE o del QE temporal.";
            }
        } else {
            return "Dependencia cuenta con comentario \"Comprometido\", del QE o del QE temporal " + authorEmailAddress
                    + " asociado al proyecto.";
        }
    }

    private JsonArray fetchDependencyComments(String isChildPad, JiraValidatorByUrlRequest dto) throws Exception {
        var query = KEY_IN + isChildPad + ")";
        var url = ApiJiraName.URL_API_JIRA_SQL + query + new JiraApiService().getQuerySuffixURL();
        var response = new JiraApiService().GetJiraAsync(dto.getUserName(), dto.getToken(), url);
        JsonObject metaData = JsonParser.parseString(response).getAsJsonObject();
        return metaData
                .getAsJsonArray(ISSUES)
                .get(0).getAsJsonObject()
                .getAsJsonObject(FIELDS)
                .getAsJsonObject("comment")
                .getAsJsonArray("comments");
    }
}
