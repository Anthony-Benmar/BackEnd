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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.bbva.common.jiraValidador.JiraValidatorConstantes.*;

public class JiraValidationMethods {
    private static final Logger LOGGER = Logger.getLogger(JiraValidationMethods.class.getName());
    private final String jiraCode;
    private final List<String> validPAD = Arrays.asList("pad3", "pad5");
    private final JsonObject jiraTicketResult;
    private boolean isInTableroDQA;
    private final boolean isEnviadoFormulario;
    private final String featureLinkCode;
    private final JsonObject featureLinkResult;
    private final String currentQ;
    private final String teamBackLogDQAId = "2461905";

    public JiraValidationMethods(String jiraCode, JsonObject jiraTicketResult, String featureLinkCode, JsonObject featureLinkResult) throws ParseException {
        this.jiraCode = jiraCode;
        this.jiraTicketResult = jiraTicketResult;
        this.featureLinkCode = featureLinkCode;
        this.featureLinkResult = featureLinkResult;
        this.isInTableroDQA = false;
        this.isEnviadoFormulario = false;
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
        String summaryComparacion = jiraTicketResult.get(FIELDS).getAsJsonObject().get(SUMMARY).toString().toLowerCase();

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
        String issueType = jiraTicketResult.getAsJsonObject(FIELDS).getAsJsonObject(ISSUETYPE).get(NAME).getAsString();

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

    public Map<String, Object> getValidatorValidateHUTType(String teamBackLogId, List<String> teamBackLogTicketIdRLB, String helpMessage, String tipoDesarrollo, String group) {
        boolean isWarning = false;

        JsonArray issuelinks = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonArray(ISSUELINKS);

        String name = null;
        String statusCategory = null;

        if (tipoDesarrollo.equalsIgnoreCase(INGESTA)) {
            if(teamBackLogTicketIdRLB.contains(teamBackLogId)){
                return buildValidationResult(MSG_RULE_INVALID, true, isWarning, helpMessage, group);
            } else {
                for (JsonElement issueLinkElement : issuelinks) {
                    JsonObject issueLink = issueLinkElement.getAsJsonObject();
                    String inward = issueLink.getAsJsonObject(TYPE).get(INWARD).getAsString();
                    if (inward.equalsIgnoreCase(IS_CHILD_ITEM_OF)) {
                        if (issueLink.has(INWARD_ISSUE)) {
                            JsonObject inwardIssue = issueLink.getAsJsonObject(INWARD_ISSUE);
                            if (inwardIssue.has(FIELDS)) {
                                JsonObject fields = inwardIssue.getAsJsonObject(FIELDS);
                                if (fields.has(ISSUETYPE) && fields.has(STATUS)) {
                                    JsonObject issuetype = fields.getAsJsonObject(ISSUETYPE);
                                    JsonObject status = fields.getAsJsonObject(STATUS);
                                    statusCategory = status.get(NAME).getAsString();
                                    name = issuetype.get(NAME).getAsString();
                                }
                            }
                        }
                    } else {
                        return buildValidationResult("No es ticket de integración", true, isWarning, helpMessage, group);
                    }
                }
                if (name != null && name.equals(STORY)) {
                    if (statusCategory != null && statusCategory.equals(DEPLOYED)) {
                        return buildValidationResult("Ticket de integración sin tickets deployados", true, isWarning, helpMessage, group);
                    } else {
                        return buildValidationResult("Ticket de integración sin tickets deployados", false, isWarning, helpMessage, group);
                    }
                } else {
                    return buildValidationResult("No es ticket de integración", true, isWarning, helpMessage, group);
                }
            }
        } else {
            return buildValidationResult(MSG_RULE_INVALID, true, isWarning, helpMessage, group);
        }
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
        String message = "";
        boolean isValid;
        boolean isWarning = false;
        List<String> prsStatusException = new ArrayList<>(List.of("DECLINED"));
        List<String> prsStatusWarning = new ArrayList<>(List.of("MERGED"));
        Map<String, String> prException = new HashMap<>();
        Map<String, String> prWarning = new HashMap<>();
        Map<String, String> prValid = new HashMap<>();
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
                    String status = jiraTicketResultPrs.get("prs").getAsJsonArray().get(0).getAsJsonObject().get(STATUS).getAsString();
                    if (prsStatusException.contains(status)){
                        prException.put(prObj.getAsJsonObject().get("url").getAsString(), prObj.getAsJsonObject().get(STATUS).getAsString());
                    } else if (prsStatusWarning.contains(status)) {
                        prWarning.put(prObj.getAsJsonObject().get("url").getAsString(), prObj.getAsJsonObject().get(STATUS).getAsString());
                    }else{
                        prValid.put(prObj.getAsJsonObject().get("url").getAsString(), prObj.getAsJsonObject().get(STATUS).getAsString());
                    }
                }
            }
        }
        int cantidadPrsValidas = prValid.size();
        int cantidadPrsWarning = prWarning.size();
        if (tipoDesarrollo.equals("PRs") || tipoDesarrollo.equals(MALLAS) || tipoDesarrolloPRs.contains(tipoDesarrollo)) {
            if (cantidadPrsValidas == 1) {
                message = "Con PR asociada: " + prValid.keySet();
                isValid = true;
            } else if (cantidadPrsValidas > 1) {
                message = "Se encontraron " + cantidadPrsValidas + " PRs asociadas: " + prValid.keySet();
                isValid = false;
                message += " Atención: No se puede tener más de una PR asociada.";
            } else {
                message = "No se detectó una PR valida asociada.";
                isValid = false;
                message += " Atención: Si la PR fue asociada correctamente, falta dar permisos de acceso a los QEs.";
            }
            if (cantidadPrsWarning>0){
                isWarning = true;
                message += " Atención: Se encontraron " + cantidadPrsWarning + " PRs asociadas en MERGED: " + prWarning.keySet();
            }
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

    public Map<String, Object> getValidationValidateSubTaskStatus(String tipoDesarrollo,String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;
        List<String> results = new ArrayList<>(SUBTASKS_BY_DEVELOP_TYPES.get(tipoDesarrollo));
        results.addAll(List.of(VB_KM, VB_SO));
        if(tipoDesarrollo.equalsIgnoreCase(MALLAS) || tipoDesarrollo.equalsIgnoreCase("host")) {
            results.add("[VB][DEV]");
        }
        results.removeIf(subtask -> subtask.contains("QA"));

        List<JsonObject> subTaskCollection = new ArrayList<>();
        JsonArray subTaskCollectionBadStatus = new JsonArray();
        List<String> subTaskLabelBadStatus = new ArrayList<>();

        JsonArray subTasks = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonArray(SUBTASKS);

        subTasks.forEach(subtask -> {
            String statusSubTask =subtask.getAsJsonObject()
                    .getAsJsonObject(FIELDS)
                    .get(SUMMARY).getAsString();

            results.forEach(result -> {
                if (statusSubTask.contains(result)){
                    subTaskCollection.add(subtask.getAsJsonObject());
                }
            });
        });
        if (subTaskCollection.isEmpty()){
            message = "No se encontraron subtareas asociadas";
        }else {
            for(JsonElement subTaskElement : subTaskCollection) {
                String status = subTaskElement.getAsJsonObject()
                        .getAsJsonObject(FIELDS)
                        .getAsJsonObject(STATUS)
                        .get(NAME).getAsString();
                if (!status.equalsIgnoreCase(ACCEPTED) && !status.equalsIgnoreCase(DISCARDED)){
                    subTaskCollectionBadStatus.add(subTaskElement);
                    subTaskLabelBadStatus.add(subTaskElement.getAsJsonObject().getAsJsonObject(FIELDS).get(SUMMARY).getAsString());
                }
            }
            if(!subTaskCollectionBadStatus.isEmpty()){
                message = "Subtareas sin estado Accepted: " + String.join(",",subTaskLabelBadStatus);
            }
            else{
                isValid = true;
                message = "Todas las subtareas tienen el estado Aceptado";
            }
        }

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationValidateSubtaskPerson(String teamBackLogId,JiraValidatorByUrlRequest dto,String tipoDesarrollo, String helpMessage, String group, List<InfoJiraProject> infoJiraProjectList) {
        String message;
        boolean isValid = false;
        boolean isWarning = false;
        List<String> messsageBadList = new ArrayList<>();
        List<String> messsageGoodList = new ArrayList<>();
        List<String> results = new ArrayList<>(SUBTASKS_BY_DEVELOP_TYPES.get(tipoDesarrollo));
        results.addAll(List.of(VB_KM, VB_SO));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        JsonArray subTasks = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonArray(SUBTASKS);
        if(subTasks.isEmpty()){
            message = "HU no cuenta con subtareas asociadas";
            return buildValidationResult(message, isValid, isWarning, helpMessage, group);
        }

        if(teamBackLogId == null || teamBackLogId.isEmpty()){
            message = "HU sin Team BackLog";
            return buildValidationResult(message, isValid, isWarning, helpMessage, group);
        } else {
            for(JsonElement subtask : subTasks){
                LocalDateTime maxDate = null;
                JsonObject maxHistory = null;
                String subtaskLabel = subtask.getAsJsonObject()
                        .getAsJsonObject(FIELDS)
                        .get(SUMMARY).getAsString();

                if(subtaskLabel.contains("QA") || subtaskLabel.contains("DEV")|| subtaskLabel.contains("GC")){
                    continue;
                }
                String subtaskStatus = subtask.getAsJsonObject()
                        .getAsJsonObject(FIELDS)
                        .getAsJsonObject(STATUS)
                        .getAsJsonPrimitive(NAME)
                        .getAsString();
                if(subtaskStatus.equals(DISCARDED)){
                    continue;
                }
                String codeJiraSubTask = subtask.getAsJsonObject().get("key").getAsString();
                var query = KEY_IN + String.join(",",  List.of(codeJiraSubTask)) + ")";

                JsonObject metaData;
                String response = null;
                var url = ApiJiraName.URL_API_JIRA_SQL + query + new JiraApiService().getQuerySuffixURL();
                try {
                    response = new JiraApiService().GetJiraAsync(dto.getUserName(), dto.getToken(), url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                metaData = JsonParser.parseString(response).getAsJsonObject();

                String subtaskAsignee = metaData
                        .getAsJsonArray(ISSUES)
                        .get(0).getAsJsonObject()
                        .getAsJsonObject(FIELDS)
                        .getAsJsonObject("assignee")
                        .get(EMAIL_ADDRESS).getAsString();
                if (subtaskAsignee == null || subtaskAsignee.isBlank()) {
                    messsageBadList.add(MSG_SUBTAREA+ subtaskLabel +" sin asignación");
                    continue;
                }
                JsonArray historiesSubstask =
                        metaData.getAsJsonObject().getAsJsonArray(ISSUES)
                                .get(0).getAsJsonObject()
                                .getAsJsonObject(CHANGELOG)
                                .getAsJsonArray(HISTORIES);
                for (int i = 0; i < historiesSubstask.size(); i++) {
                    JsonObject history = historiesSubstask.get(i).getAsJsonObject();
                    String createdDate = history.get(CREATED).getAsString();
                    LocalDateTime historyDate = LocalDateTime.parse(createdDate, formatter);
                    JsonArray itemsHistory = history.getAsJsonArray(ITEMS);

                    if (itemsHistory != null && !itemsHistory.isEmpty()) {
                        boolean hasMatchingItem = false;

                        for (JsonElement itemElement : itemsHistory) {
                            JsonElement itemToString = itemElement.getAsJsonObject().get("toString");
                            if(itemToString != null && !itemToString.isJsonNull()) {
                                String toString = itemToString.getAsString();
                                if (ACCEPTED.equals(toString)) {
                                    hasMatchingItem = true;
                                    break;
                                }
                            }
                        }

                        if (hasMatchingItem) {
                            if (maxDate == null || historyDate.isAfter(maxDate)) {
                                maxDate = historyDate;
                                maxHistory = history;
                            }
                        }
                    }
                }
                if (maxHistory == null){
                    messsageBadList.add(MSG_SUBTAREA+ subtaskLabel +" no tiene estado Accepted");
                    continue;
                }

                String voboPerson = maxHistory.getAsJsonObject("author").get(EMAIL_ADDRESS).getAsString();
                if (!voboPerson.equalsIgnoreCase(subtaskAsignee)){
                    messsageBadList.add(MSG_SUBTAREA+ subtaskLabel +" VoBo de "+ voboPerson +" no es el mismo asignado en la subtarea");
                    continue;
                }
                List<InfoJiraProject> projectsFiltrados = new ArrayList<>();
                for (Map.Entry<String, Map<String, Object>> entry : SUBTASKS_TYPE_OWNER.entrySet()) {
                    List<String> items = (List<String>) entry.getValue().get(ITEMS);
                    if (items != null && items.contains(subtaskLabel) &&
                            (boolean) entry.getValue().get("validateEmailFromLideres")) {
                        projectsFiltrados = infoJiraProjectList.stream().filter(
                                project -> ((List<String>) entry.getValue().get("rol")).contains(project.getProjectRolType())
                                        && project.getTeamBackLogId().equals(teamBackLogId)
                        ).toList();
                    }
                }
                if (projectsFiltrados.isEmpty()){
                    messsageBadList.add("No se encontró persona para este rol para "+ subtaskLabel +" en SIDE");
                }else{
                    boolean existeVoboPerson = projectsFiltrados.stream()
                            .anyMatch(obj -> obj.getParticipantEmail().equals(voboPerson));
                    if (existeVoboPerson){
                        messsageGoodList.add(voboPerson + " para "+ subtaskLabel + " es valida");
                    }
                }
            }
            if(messsageBadList.isEmpty() && messsageGoodList.isEmpty()){
                messsageBadList.add("No se encontraron subtareas asociadas");
            }
        }
        if (messsageBadList.isEmpty() && !messsageGoodList.isEmpty()){
            isValid = true;
        }
        messsageGoodList.addAll(messsageBadList);
        message = String.join(". ",messsageGoodList);

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationValidateSubTaskValidateContractor(JiraValidatorByUrlRequest dto,String tipoDesarrollo, String helpMessage, String group) {
        AtomicReference<String> message = new AtomicReference<>("");
        AtomicBoolean isValid = new AtomicBoolean(false);

        boolean isWarning = false;

        List<String> results = new ArrayList<>(SUBTASKS_BY_DEVELOP_TYPES.get(tipoDesarrollo));
        results.addAll(List.of(VB_KM, VB_SO,"[VB][ALPHA]"));
        JsonArray subTasks = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonArray(SUBTASKS);

        List<String> messsageBadList = new ArrayList<>();
        List<String> messsageGoodList = new ArrayList<>();

        for(JsonElement subtask: subTasks){
            String subtaskLabel = subtask.getAsJsonObject()
                    .getAsJsonObject(FIELDS)
                    .get(SUMMARY).getAsString();
            if(subtaskLabel.contains("QA") || subtaskLabel.contains("DEV")){
                continue;
            }
            String subtaskStatus = subtask.getAsJsonObject()
                    .getAsJsonObject(FIELDS)
                    .getAsJsonObject(STATUS)
                    .getAsJsonPrimitive(NAME)
                    .getAsString();
            if(subtaskStatus.equals(DISCARDED)){
                continue;
            }
            String codeJiraSubTask = subtask.getAsJsonObject().get("key").getAsString();
            var query = KEY_IN + String.join(",", codeJiraSubTask) + ")";
            JsonObject metaData;
            try {
                var url = ApiJiraName.URL_API_JIRA_SQL + query + new JiraApiService().getQuerySuffixURL();
                var response = new JiraApiService().GetJiraAsync(dto.getUserName(),dto.getToken(),url);
                metaData = JsonParser.parseString(response).getAsJsonObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            JsonObject assignee = metaData
                    .getAsJsonArray(ISSUES)
                    .get(0).getAsJsonObject()
                    .getAsJsonObject(FIELDS)
                    .getAsJsonObject("assignee");
            String asigneeEmail = assignee.get(EMAIL_ADDRESS).getAsString();
            if(!asigneeEmail.isBlank()){
                if (asigneeEmail.contains(".contractor")) {
                    messsageBadList.add("Subtarea invalida " + subtaskLabel +" asignada a "+ asigneeEmail + " no es Interno BBVA");
                }
                else {
                    messsageGoodList.add("Subtarea valida " + subtaskLabel +" asignada a "+ asigneeEmail + " es Interno BBVA");
                }
            }
            else {
                messsageBadList.add("Subtarea invalida " + subtaskLabel + " no tiene correo asignado");
            }
        }

        if (!messsageBadList.isEmpty()) {
            messsageBadList.addAll(messsageGoodList);
            message.set(String.join(". ", messsageBadList));
            isValid.set(false);
        } else {
            message.set(String.join(". ", messsageGoodList));
            isValid.set(true);
        }
        return buildValidationResult(message.get(), isValid.get(), isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationAcceptanceCriteria(List<String> teamBackLogTicketIdRLB,String tipoDesarrollo, String helpMessage, String group, List<InfoJiraProject> infoJiraProjectList) {
        String message;
        boolean isValid = false;
        boolean isWarning = false;
        Map<String, Object> validAcceptanceCriteriaObject = CRITERIA_BY_DEVELOP_TYPES.get(tipoDesarrollo);

        String acceptanceCriteria = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .get("customfield_10260").getAsString();

        acceptanceCriteria = acceptanceCriteria.replaceAll("[\\s\\u00A0]+", " ").trim();
        if(tipoDesarrollo.equalsIgnoreCase(MALLAS)){
            String teamBackLogTicketId = jiraTicketResult
                    .getAsJsonObject()
                    .getAsJsonObject(FIELDS)
                    .get("customfield_13301").getAsString();

            if(teamBackLogTicketIdRLB.contains(teamBackLogTicketId)){ //TICKET RELIABILITY
                if (!acceptanceCriteria.isEmpty()) {
                    if (validAcceptanceCriteriaObject != null) {

                        String expectedPattern = (String) validAcceptanceCriteriaObject.get(TEXTO);

                        expectedPattern = expectedPattern
                                .replace("{0}", "[A-Za-z\\s-/.\\&]+");

                        String regexPattern = expectedPattern
                                .replaceAll("\\s+", "\\\\s+")
                                .replace("\\.", "\\\\.");
                        regexPattern = regexPattern
                                .replace("{1}", "(SDATOOL-\\d{5}|SDATOOL\\s+\\d{5})(.*?)\\s*,")+"?";


                        Pattern pattern = Pattern.compile(regexPattern);
                        Matcher matcher = pattern.matcher(acceptanceCriteria);

                        if (matcher.matches()) {
                            message = String.format(MSG_RULE_VALID, acceptanceCriteria);
                            isValid = true;
                        } else {
                            message = MSG_RULE_CRITERIOFORMATO;
                        }

                    } else {
                        message = MSG_RULE_TIPODESARROLLO;
                    }
                } else {
                    message = MSG_RULE_CRITEROACEPTACION;
                }
            } else{

                if (!acceptanceCriteria.isEmpty()) {
                    if (validAcceptanceCriteriaObject != null) {
                        String expectedPattern = (String) validAcceptanceCriteriaObject.get(TEXTO);

                        expectedPattern = expectedPattern
                                .replace("{0}", "[A-Za-z\\s-/.\\&]+");

                        String regexPattern = expectedPattern
                                .replaceAll("\\s+", "\\\\s+")
                                .replaceAll("\\.", "\\\\.");
                        regexPattern = regexPattern
                                .replace("{1}", "(SDATOOL-\\d{5}|SDATOOL\\s+\\d{5})(.*?)\\s*,")+"?";


                        Pattern pattern = Pattern.compile(regexPattern);
                        Matcher matcher = pattern.matcher(acceptanceCriteria);

                        if (matcher.matches()) {
                            message = String.format(MSG_RULE_VALID, acceptanceCriteria);
                            isValid = true;
                        } else {
                            message = MSG_RULE_CRITERIOFORMATO;
                        }
                    } else {
                        message = MSG_RULE_TIPODESARROLLO;
                    }
                } else {
                    message = MSG_RULE_CRITEROACEPTACION;
                }
            }

        } else{
            if (!acceptanceCriteria.isEmpty()) {
                if (validAcceptanceCriteriaObject != null) {
                    String expectedPattern = (String) validAcceptanceCriteriaObject.get(TEXTO);
                    String[] palabras = expectedPattern.split("\\s+");

                    if (palabras.length >= 11) {
                        message = String.format(MSG_RULE_VALID, acceptanceCriteria);
                        isValid = true;
                    } else {
                        message = MSG_RULE_CRITERIOFORMATO;
                    }

                } else {
                    message = MSG_RULE_TIPODESARROLLO;
                }
            } else {
                message = MSG_RULE_CRITEROACEPTACION;
            }

        }
        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationTeamAssigned(String tipoDesarrollo, boolean validacionEnvioFormulario, String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        String jiraTicketStatus = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonObject(STATUS)
                .get(NAME).getAsString();

        Map<String, String> storyMap = new HashMap<>();
        storyMap.put(LABEL, TEAM_BACKLOG);
        storyMap.put(FIELD, "teamId");
        Map<String, String> dependencyMap = new HashMap<>();
        dependencyMap.put(LABEL, "Receptor Team");
        dependencyMap.put(FIELD, "receptorTeamId");
        Map<String, Map<String, String>> teamFieldLabelByIssueType = new HashMap<>();
        teamFieldLabelByIssueType.put("Historia", storyMap);
        teamFieldLabelByIssueType.put(STORY, storyMap);
        teamFieldLabelByIssueType.put(DEPENDENCY, storyMap); //dependencyMap
        String issueType = jiraTicketResult.getAsJsonObject(FIELDS)
                .getAsJsonObject(ISSUETYPE)
                .get(NAME).getAsString();

        String currentTeamFieldLabel = (teamFieldLabelByIssueType.containsKey(issueType)) ? teamFieldLabelByIssueType.get(issueType).get(LABEL) : "";

        List<String> estadosExtraMallasHost = Arrays.asList(READY, TEST, READY_TO_VERIFY);
        List<String> statusTableroDQA = new ArrayList<>();
        statusTableroDQA.add(READY);
        statusTableroDQA.add(IN_PROGRESS);
        statusTableroDQA.add(TEST);
        statusTableroDQA.add(READY_TO_VERIFY);
        statusTableroDQA.add(READY_TO_DEPLOY);
        statusTableroDQA.add(DEPLOYED);
        statusTableroDQA.add(ACCEPTED);
        statusTableroDQA.replaceAll(String::toLowerCase);

        JsonArray histories = this.jiraTicketResult.getAsJsonObject(CHANGELOG).getAsJsonArray(HISTORIES);

        for (JsonElement historyElement : histories) {
            JsonObject history = historyElement.getAsJsonObject();

            if (history.has(ITEMS)) {
                JsonArray items = history.getAsJsonArray(ITEMS);
                for (JsonElement itemElement : items) {
                    JsonObject item = itemElement.getAsJsonObject();

                    if (item.has(FIELD) && item.get(FIELD).getAsString().equals(currentTeamFieldLabel)) {
                        String from = item.get("from").getAsString();
                        String to = item.get("to").getAsString();

                        HashMap<String, String> currentTeam = new HashMap<>();
                        currentTeam.put("from", from);
                        currentTeam.put("id", to);

                        if (currentTeam != null && (currentTeam.get("id").equals(teamBackLogDQAId))) {
                            this.isInTableroDQA = true;
                            message = "Asignado a Tablero de DQA";
                            isValid = true;
                        } else {
                            message = "No está en el Tablero de DQA";
                            statusTableroDQA.replaceAll(String::toLowerCase);
                            if (statusTableroDQA.contains( jiraTicketStatus.trim().toLowerCase())) {
                                if (validacionEnvioFormulario) {
                                    StringBuilder sb = new StringBuilder(message);
                                    sb.append("Atención: No olvidar que para regresar el ticket a DQA, se debe cambiar el estado del ticket y la Subtarea DQA");

                                    if (tipoDesarrollo.equals(MALLAS) || tipoDesarrollo.equals("HOST")) {
                                        sb.append(String.join(", ", estadosExtraMallasHost));
                                    } else {
                                        sb.append(READY);
                                    }
                                    message = sb.toString();
                                }
                                isValid = false;
                            }
                        }
                    }
                }
            }
        }
        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationValidateJIRAStatus(String tipoDesarrollo, String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        var jiraTicketStatus = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonObject(STATUS)
                .get(NAME).getAsString();
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
                if (this.isInTableroDQA && this.isEnviadoFormulario) {
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
        String featureLinkStatus = featureLinkResult
                .getAsJsonArray(ISSUES)
                .get(0).getAsJsonObject()
                .getAsJsonObject(FIELDS)
                .getAsJsonObject(STATUS)
                .get(NAME).getAsString();

        if (validStatus.contains(featureLinkStatus)) {
            isValid = true;
        }
        message = "Con estado " + featureLinkStatus;

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationFeatureLinkProgramIncrement(JiraValidatorByUrlRequest dto, String helpMessage, String group){
        String message;
        boolean isValid;
        boolean isWarning = false;
        Map<String, Object> validationFeatureResult = getValidationFeatureLink(helpMessage, group);
        if (!(boolean) validationFeatureResult.get(ISVALID)) {
            return validationFeatureResult;
        }
        JsonArray programIncrement = null;
        if (featureLinkResult.has(ISSUES) &&
                !featureLinkResult.getAsJsonArray(ISSUES).isEmpty() &&
                featureLinkResult.getAsJsonArray(ISSUES)
                        .get(0).getAsJsonObject().getAsJsonObject(FIELDS)
                        .has(CUSTOMFIELD_10264)) {

            programIncrement = featureLinkResult
                    .getAsJsonArray(ISSUES)
                    .get(0).getAsJsonObject()
                    .getAsJsonObject(FIELDS)
                    .get(CUSTOMFIELD_10264)
                    .getAsJsonArray();
        }

        if (programIncrement == null || programIncrement.isJsonNull() || programIncrement.isEmpty()) {
            message = "Sin Program Increment";
            String tipoIncidencia = jiraTicketResult.get(FIELDS)
                    .getAsJsonObject()
                    .get(ISSUETYPE)
                    .getAsJsonObject()
                    .get(NAME)
                    .getAsString();
            isValid = !tipoIncidencia.isEmpty();
            if (isValid) {
                message = "Sin Program Increment, pero con tipo de incidencia: " + tipoIncidencia;
                isWarning = true;
            }
        } else {
            message = "Con Program Increment " + programIncrement;
            isValid = true;
            String jiraTicketStatus = jiraTicketResult.get(FIELDS)
                    .getAsJsonObject().get(STATUS).getAsJsonObject().get(NAME).getAsString();

            boolean containsCurrentQ = false;
            for (JsonElement element : programIncrement) {
                if (element.getAsString().equals(currentQ)) {
                    containsCurrentQ = true;
                    break;
                }
            }

            if (!jiraTicketStatus.equals(DEPLOYED) && !containsCurrentQ) {
                message += " Atención: El Program Increment debe contener al Q actual (En este caso " + currentQ + ") cuando el ticket este en revisión, " + MSG_COORDINATION_MESSAGE;
                isValid = false;
            }
        }
        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationFeatureLinkRLB(String teamBackLogId,List<String> teamBackLogTicketIdRLB, JiraValidatorByUrlRequest dto, String tipoDesarrollo, String helpMessage, String group){
        String message = "";
        boolean isValid;
        boolean isWarning = false;
        List<String> validStatus = List.of("INC", "PRB", "PB");

        Map<String, Object> validationFeatureResult = getValidationFeatureLink(helpMessage, group);
        if (!(boolean) validationFeatureResult.get(ISVALID)) {
            return validationFeatureResult;
        }
        if(!teamBackLogTicketIdRLB.contains(teamBackLogId)){
            return buildValidationResult(MSG_RULE_INVALID, true,isWarning,helpMessage,group);
        }
        JsonArray featureLinkLabels = null;
        if (featureLinkResult.has(ISSUES) &&
                !featureLinkResult.getAsJsonArray(ISSUES).isEmpty() &&
                featureLinkResult.getAsJsonArray(ISSUES)
                        .get(0).getAsJsonObject().getAsJsonObject(FIELDS)
                        .has(LABELS)) {
            featureLinkLabels = featureLinkResult
                    .getAsJsonArray(ISSUES)
                    .get(0).getAsJsonObject()
                    .getAsJsonObject(FIELDS)
                    .getAsJsonArray(LABELS);
        }
        if (featureLinkLabels == null || featureLinkLabels.isJsonNull() || featureLinkLabels.isEmpty()) {
            message += "El ticket no es una incidencia o problema o no contiene los labels identificadores "+String.join(",", validStatus)+".";
            isValid = true;
        }else {
            boolean containsValidStatus = false;
            for (int i = 0; i < featureLinkLabels.size(); i++) {
                String label = featureLinkLabels.get(i).getAsString();
                for (String status : validStatus) {
                    if (label.startsWith(status)) {
                        containsValidStatus = true;
                        break;
                    }
                }
                if (containsValidStatus) break;
            }
            if (containsValidStatus) {
                message += "El ticket es una incidencia o problema, contiene los labels correspondientes";
                isValid = true;
            } else {
                message += "El ticket debe corresponder a un evolutivo o solicitud interna para no llevar los labels correspondientes";
                isValid = true;
                isWarning = true;
            }
        }
        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationIFRS9(String helpMessage, String group) {
        String message = "";
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

    public Map<String, Object> getValidationValidateSubTask(String tipoDesarrollo, String helpMessage, String group) {
        String message;
        boolean isValid = false;
        boolean isWarning = false;

        List<String> aditionalSpecialSubtask = List.of(VB_KM, VB_SO);
        List<String> aditionalSpecialLabels = List.of("datioRutaCritica", "JobsHuerfanos");

        JsonArray labels = jiraTicketResult.getAsJsonObject(FIELDS).getAsJsonArray(LABELS);
        List<String> foundSpecialLabel = extractMatchingLabels(labels, aditionalSpecialLabels);

        List<String> requiredSubTasks = SUBTASKS_BY_DEVELOP_TYPES.getOrDefault(tipoDesarrollo, List.of());
        JsonArray subTasks = jiraTicketResult.getAsJsonObject(FIELDS).getAsJsonArray(SUBTASKS);
        SubTaskValidationResult subTaskResult = validateSubTasks(subTasks, requiredSubTasks, foundSpecialLabel, aditionalSpecialSubtask);

        if (subTaskResult.foundSubTasks.containsAll(requiredSubTasks)) {
            message = buildValidSubTasksMessage(subTaskResult, tipoDesarrollo, foundSpecialLabel, aditionalSpecialSubtask);
            isValid = subTaskResult.isValid;
            isWarning = subTaskResult.isWarning;
        } else {
            List<String> missingSubTasks = new ArrayList<>(requiredSubTasks);
            missingSubTasks.removeAll(subTaskResult.foundSubTasks);
            message = buildMissingSubTasksMessage(missingSubTasks, subTaskResult, tipoDesarrollo, foundSpecialLabel, aditionalSpecialSubtask);
            isValid = subTaskResult.isValid;
            isWarning = subTaskResult.isWarning;
        }

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    private List<String> extractMatchingLabels(JsonArray labels, List<String> aditionalSpecialLabels) {
        List<String> foundSpecialLabel = new ArrayList<>();
        for (JsonElement label : labels) {
            String labelString = label.getAsString();
            if (aditionalSpecialLabels.contains(labelString)) {
                foundSpecialLabel.add(labelString);
            }
        }
        return foundSpecialLabel;
    }

    private SubTaskValidationResult validateSubTasks(JsonArray subTasks, List<String> requiredSubTasks, List<String> foundSpecialLabel, List<String> aditionalSpecialSubtask) {
        List<String> foundSubTasks = new ArrayList<>();
        List<String> additionalSubTasks = new ArrayList<>();
        List<String> foundSpecialSubtasks = new ArrayList<>();

        for (JsonElement subTask : subTasks) {
            String subTaskLabel = subTask.getAsJsonObject().get(FIELDS).getAsJsonObject().get(SUMMARY).getAsString();
            if (requiredSubTasks.contains(subTaskLabel)) {
                foundSubTasks.add(subTaskLabel);
            } else {
                additionalSubTasks.add(subTaskLabel);
                if (!foundSpecialLabel.isEmpty() || aditionalSpecialSubtask.contains(subTaskLabel)) {
                    foundSpecialSubtasks.add(subTaskLabel);
                }
            }
        }

        return new SubTaskValidationResult(foundSubTasks, additionalSubTasks, foundSpecialSubtasks);
    }

    private String buildValidSubTasksMessage(SubTaskValidationResult result, String tipoDesarrollo, List<String> foundSpecialLabel, List<String> aditionalSpecialSubtask) {
        String message = "Todas las subtareas requeridas fueron encontradas.";
        if (!result.additionalSubTasks.isEmpty()) {
            message += " También se encontraron subtareas adicionales: " + String.join(", ", result.additionalSubTasks) + ". ";
            message += handleSpecialSubTasks(tipoDesarrollo, foundSpecialLabel, result.foundSpecialSubtasks, aditionalSpecialSubtask);
        }
        return message;
    }

    private String buildMissingSubTasksMessage(List<String> missingSubTasks, SubTaskValidationResult result, String tipoDesarrollo, List<String> foundSpecialLabel, List<String> aditionalSpecialSubtask) {
        String message = "Faltan las siguientes subtareas: " + String.join(", ", missingSubTasks);
        if (!result.additionalSubTasks.isEmpty()) {
            message += " Además, se encontraron subtareas adicionales: " + String.join(", ", result.additionalSubTasks);
            message += handleSpecialSubTasks(tipoDesarrollo, foundSpecialLabel, result.foundSpecialSubtasks, aditionalSpecialSubtask);
        }
        return message;
    }

    private String handleSpecialSubTasks(String tipoDesarrollo, List<String> foundSpecialLabel, List<String> foundSpecialSubtasks, List<String> aditionalSpecialSubtask) {
        if (tipoDesarrollo.equals(MALLAS)) {
            if (!foundSpecialLabel.isEmpty() && foundSpecialSubtasks.isEmpty()) {
                return MSG_RULE_NOSUBTAREA + String.join(", ", aditionalSpecialSubtask);
            } else if (foundSpecialLabel.isEmpty() && !foundSpecialSubtasks.isEmpty()) {
                return "Se recomienda validar las subtareas adicionales: " + String.join(", ", aditionalSpecialSubtask)
                        + " para casos de jobs eliminados, huérfanos, ruta crítica. Puede estar pendiente el label correspondiente.";
            }
        } else {
            if (!foundSpecialLabel.isEmpty() && foundSpecialSubtasks.isEmpty()) {
                return MSG_RULE_NOSUBTAREA + String.join(", ", aditionalSpecialSubtask);
            } else if (foundSpecialLabel.isEmpty() && !foundSpecialSubtasks.isEmpty()) {
                return MSG_RULE_RECOMENDATIONSUBTAREA + String.join(", ", aditionalSpecialSubtask)
                        + " para casos de jobs de ruta crítica. Puede estar pendiente el label correspondiente.";
            }
        }
        return "";
    }

    private static class SubTaskValidationResult {
        List<String> foundSubTasks;
        List<String> additionalSubTasks;
        List<String> foundSpecialSubtasks;
        boolean isValid = true;
        boolean isWarning = false;

        SubTaskValidationResult(List<String> foundSubTasks, List<String> additionalSubTasks, List<String> foundSpecialSubtasks) {
            this.foundSubTasks = foundSubTasks;
            this.additionalSubTasks = additionalSubTasks;
            this.foundSpecialSubtasks = foundSpecialSubtasks;
        }
    }

    public Map<String, Object> getValidationValidateImpactLabel(String helpMessage, String group, String tipoDesarrollo) {
        String message;
        boolean isValid = false;
        boolean isWarning = false;
        if (tipoDesarrollo.equals("HOST") || tipoDesarrollo.equals(MALLAS)) {
            List<String> jiraTicketImpactLabelList = new ArrayList<>();
            JsonElement impactLabelElement;
            JsonObject fieldsObject = jiraTicketResult.getAsJsonObject(FIELDS);

            if (fieldsObject.has("customfield_10267")) {
                impactLabelElement = fieldsObject.get("customfield_10267");

                if (impactLabelElement != null && impactLabelElement.isJsonArray()) {
                    JsonArray impactLabelArray = impactLabelElement.getAsJsonArray();
                    for (JsonElement element : impactLabelArray) {
                        jiraTicketImpactLabelList.add(element.getAsString());
                    }
                }
            }
            else {
                message = "No se tiene Impact Label definidos";
                return buildValidationResult(message, isValid, isWarning, helpMessage, group);
            }

            List<String> validImpactLabel = Arrays.asList("AppsInternos", "Datio");
            List<String> validImpactLabelListHost = Arrays.asList("DataHub", "Host", "Plataforma_InformacionalP11");
            List<String> validImpactLabelFinalList = tipoDesarrollo.equals("HOST") ? validImpactLabelListHost : validImpactLabel;
            List<String> impactLabelNotExistsList = new ArrayList<>();

            for (String impactLabel : validImpactLabelFinalList) {
                if (!jiraTicketImpactLabelList.contains(impactLabel)) {
                    impactLabelNotExistsList.add(impactLabel);
                }
            }

            if (impactLabelNotExistsList.isEmpty()) {
                message = "Impact Label " + ", " + validImpactLabelFinalList;
                isValid = true;
            } else {
                message = "Falta Impact Label: " + ", " + impactLabelNotExistsList;
                if (!jiraTicketImpactLabelList.isEmpty()) {
                    message += " Se tiene: " + ", " + jiraTicketImpactLabelList;
                } else {
                    message += " No se tiene Impact Label definidos";
                }
            }
        }
        else{
            message = MSG_RULE_INVALID;
            isValid = true;
        }

        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationFixVersion(String tipoDesarrollo, String helpMessage, String group) {
        String message = "";
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
        String message = "";
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
            String attachmentLabel = filename.split("-")[0].replaceAll("\\s+", "");;
            if (requiredAttachments.contains(attachmentLabel)) {
                foundAttachments.add(attachmentLabel);
            }
        }

        if (foundAttachments.containsAll(requiredAttachments)) {
            message = "Todos los adjuntos requeridos fueron encontrados.";
            isValid = true;
            return buildValidationResult(message, isValid, isWarning, helpMessage, group);
        } else {
            List<String> missingAttachments = new ArrayList<>(requiredAttachments);
            missingAttachments.removeAll(foundAttachments);
            message = "Faltan los siguientes adjuntos: " + String.join(", ", missingAttachments);
            isValid = false;
            return buildValidationResult(message, isValid, isWarning, helpMessage, group);
        }
   }

   public Map<String, Object> getValidationProductivizacionIssueLink(String tipoDesarrollo, String helpMessage, String group){
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

       JsonArray issuelinks = jiraTicketResult
               .getAsJsonObject(FIELDS)
               .getAsJsonArray(ISSUELINKS);

       String name = null;
       String statusCategory = null;

       JsonArray issueLinkStory = new JsonArray();
       if (tipoDesarrollo.equalsIgnoreCase("productivizacion")) {
           for (JsonElement issueLinkElement : issuelinks) {
               String type  = issueLinkElement.getAsJsonObject().getAsJsonObject(TYPE).get(INWARD).getAsString();
               JsonElement inwardIssue = issueLinkElement
                       .getAsJsonObject()
                       .getAsJsonObject(INWARD_ISSUE);
               if (type.equals(IS_CHILD_ITEM_OF) && inwardIssue != null) {
                   name = inwardIssue
                           .getAsJsonObject()
                           .getAsJsonObject(FIELDS).getAsJsonObject(ISSUETYPE).get(NAME).getAsString();
                   if(name.equals(STORY)){
                       issueLinkStory.add(issueLinkElement);
                   }
               }
           }

           if (!issueLinkStory.isEmpty()) {
               for (JsonElement issueLinkElement : issueLinkStory) {
                   statusCategory = issueLinkElement.getAsJsonObject()
                           .getAsJsonObject(INWARD_ISSUE)
                           .getAsJsonObject(FIELDS)
                           .getAsJsonObject(STATUS)
                           .get(NAME).getAsString().toLowerCase();
                   if (statusCategory.equalsIgnoreCase(DEPLOYED)) {
                       message = "Todos los tickets asociados se encuentran deployados";
                       isValid = true;
                   } else {
                       message = "No todos los tickets asociados se encuentran deployados";
                       break;
                   }
               }
           } else{
               message = "No se encontraron tickets asociados de tipo Story";
           }
       } else {
           isValid = true;
           message = MSG_RULE_INVALID;
       }

       return buildValidationResult(message, isValid, isWarning, helpMessage, group);
   }

   public Map<String, Object> getValidationLabels(String tipoDesarrollo, String helpMessage, String group){
        String message = "";
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

        if (foundLabels.containsAll(requiredLabels)) {
            message = "Todas las etiquetas correspondientes fueron encontradas.";
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

        String extractedContent = "";
        JsonArray changelog = jiraTicketResult
                .getAsJsonObject(CHANGELOG)
                .getAsJsonArray(HISTORIES);

        Date oldestDate = new SimpleDateFormat("yyyy-MM-dd").parse("9999-12-31");
        String from = "";
        String fromString = "";
        for (JsonElement history : changelog) {
            JsonObject historyObj = history.getAsJsonObject();
            String created = historyObj.get(CREATED).getAsString();
            Date createdDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(created);

            if (createdDate.before(oldestDate)) {
                JsonArray items = historyObj.getAsJsonArray(ITEMS);
                String field = items.get(0).getAsJsonObject().get(FIELD).getAsString();
                if (field.equals(TEAM_BACKLOG)) {
                    if (items.get(0).getAsJsonObject().get("fromString").isJsonNull()) {
                        from = "";
                        fromString = "";
                    } else {
                        from = items.get(0).getAsJsonObject().get("from").getAsString();
                        fromString = items.get(0).getAsJsonObject().get("fromString").getAsString();
                    }
                    Pattern pattern = Pattern.compile("<span style=\"color: #fff\">(.*?)</span>");
                    Matcher matcher = pattern.matcher(fromString);
                    if (matcher.find()) {
                        extractedContent = matcher.group(1);
                    }
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

    private Map<String, Object> validateDependencyLink(String helpMessage, String group){
        JsonArray issueLinks = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonArray(ISSUELINKS);
        if (issueLinks == null || issueLinks.isEmpty()) {
            return buildValidationResult(MSG_RULE_NODEPENDENCY, false, false, helpMessage, group);
        }
        return Map.of();
    }

    public Map<String, Object> getValidationDependency(String teamBackLogId,List<String> teamBackLogTicketIdRLB,String helpMessage, String group) {
        String message;
        boolean isValid = false;
        boolean isWarning = false;

        String statusDependencyTicket;

        JsonArray issueLinks = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonArray(ISSUELINKS);

        if(teamBackLogTicketIdRLB.contains(teamBackLogId)){
            message = "Proviene del tabero RLB, por lo tanto, no tiene una dependencia asociada.";
            isValid = true;
        } else {
            if (issueLinks == null || issueLinks.isEmpty()) {
                message = MSG_RULE_NODEPENDENCY;
                return buildValidationResult(message, isValid, isWarning, helpMessage, group);
            } else {
                List<String> statusDependencyCollection = new ArrayList<>();
                List<String> dependencyPadCollection = new ArrayList<>();
                for (JsonElement issueLinkElement : issueLinks) {
                    String type = issueLinkElement.getAsJsonObject().getAsJsonObject(TYPE).get(INWARD).getAsString();
                    JsonElement inwardIssue = issueLinkElement
                            .getAsJsonObject()
                            .getAsJsonObject(INWARD_ISSUE);
                    if (type.equals(IS_CHILD_ITEM_OF) && inwardIssue != null) {
                        String issuetype = inwardIssue.getAsJsonObject().getAsJsonObject(FIELDS).getAsJsonObject(ISSUETYPE).get(NAME).getAsString();
                        if (issuetype.equalsIgnoreCase(DEPENDENCY)) {
                            statusDependencyTicket = inwardIssue.getAsJsonObject()
                                    .getAsJsonObject(FIELDS)
                                    .getAsJsonObject(STATUS)
                                    .get(NAME).getAsString().toLowerCase();
                            statusDependencyCollection.add(statusDependencyTicket);
                            dependencyPadCollection
                                    .add(issueLinkElement
                                            .getAsJsonObject()
                                            .getAsJsonObject(INWARD_ISSUE)
                                            .get("key").getAsString());
                        }
                    }
                }
                if (statusDependencyCollection.isEmpty()) {
                    message = MSG_RULE_NODEPENDENCY;
                    return buildValidationResult(message, isValid, isWarning, helpMessage, group);
                }

                boolean allInProgress = statusDependencyCollection.stream().allMatch(status -> status.equalsIgnoreCase(IN_PROGRESS));

                if (allInProgress) {
                    isValid = true;
                    message = "Todas las dependencias se encuentran en el estado que corresponde.";
                } else {
                    isWarning = true;
                    StringBuilder urlMessage = new StringBuilder("Las siguientes dependencias: ");
                    for (int i = 0; i < statusDependencyCollection.size(); i++) {
                        if (!statusDependencyCollection.get(i).equals(IN_PROGRESS)) {
                            urlMessage.append(ApiJiraName.URL_API_BROWSE).append(dependencyPadCollection.get(i)).append(", ");
                        }
                    }
                    urlMessage.setLength(urlMessage.length() - 2); // Remove trailing comma and space
                    urlMessage.append(" no se encuentran en el estado correspondiente (In Progress).");
                    message = urlMessage.toString();
                }
            }
        }
        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationDependencyFeatureVsHUTFeature(String teamBackLogId,List<String> teamBackLogTicketIdRLB, JiraValidatorByUrlRequest dto, String helpMessage, String group, List<InfoJiraProject> infoJiraProjectList) {
        boolean isValid = true;
        String message = "Todas las dependencias tienen el mismo feature link";
        boolean isWarning = false;
        String isChildPadName ="";
        JsonArray issueLinks = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonArray(ISSUELINKS);

        if(teamBackLogTicketIdRLB.contains(teamBackLogId)){
            message = "Proviene del tabero RLB, por lo que no tiene dependencia asociada y, en consecuencia, esta regla no es aplicable.";
        } else {
            if (issueLinks == null || issueLinks.isEmpty()) {
                isValid = false;
                message = MSG_RULE_NODEPENDENCY;
                return buildValidationResult(message, isValid, isWarning, helpMessage, group);
            } else {
                List<String> isChildPadNameCollection = new ArrayList<>();
                for (JsonElement issueLinkElement : issueLinks) {
                    String type = issueLinkElement.getAsJsonObject().getAsJsonObject(TYPE).get(INWARD).getAsString();
                    JsonElement inwardIssue = issueLinkElement
                            .getAsJsonObject()
                            .getAsJsonObject(INWARD_ISSUE);
                    if (type.equals(IS_CHILD_ITEM_OF) && inwardIssue != null) {
                        String issuetype = inwardIssue.getAsJsonObject().getAsJsonObject(FIELDS).getAsJsonObject(ISSUETYPE).get(NAME).getAsString();
                        if (issuetype.equalsIgnoreCase(DEPENDENCY)) {
                            isChildPadName = issueLinkElement
                                    .getAsJsonObject()
                                    .getAsJsonObject(INWARD_ISSUE)
                                    .get("key").getAsString();
                            isChildPadNameCollection.add(isChildPadName);
                        }
                    }
                }

                try {
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
                            isValid = false;
                            message = "No todas las dependencias tienen el mismo features link";
                            break;
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }


    public Map<String, Object> getValidationBoardProject(String teamBackLogId,JiraValidatorByUrlRequest dto, String helpMessage, String feature_link, String group,List<InfoJiraProject> infoJiraProjectList) {
        String message = "El tablero es valido";
        boolean isValid = false;
        boolean isWarning = false;
        String summaryTicket =  jiraTicketResult.getAsJsonObject()
                .getAsJsonObject(FIELDS)
                .get(SUMMARY).getAsString();
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
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;
        String alphaVoBo = "[VB][ALPHA]";
        List<String> alphaUuaas = List.of("KLIM", "KFUL", "ATAU", "KSKR",
                "KMOL", "KAGE", "KSAN", "W1BD", "KCOL");
        JsonArray atachments = jiraTicketResult
                .getAsJsonObject(FIELDS).get(ATTACHMENT).getAsJsonArray();
        List<String> atachmentFilenameList = new ArrayList<>();
        List<String> matchedUuaas;
        if (tipoDesarrollo.equalsIgnoreCase(MALLAS)) {
            for (JsonElement attachment : atachments){
                String filename = attachment.getAsJsonObject().get("filename").getAsString();
                atachmentFilenameList.add(filename);
            }
            if(!atachmentFilenameList.isEmpty()){
                matchedUuaas = alphaUuaas.stream()
                        .filter(uuaa -> atachmentFilenameList.stream().anyMatch(fileName -> fileName.contains(uuaa)))
                        .collect(Collectors.toList());
                if(matchedUuaas.isEmpty()){
                    message = "No se encontro UUAA bajo dominio de Alpha ";
                    isValid = true;
                }
                else {
                    JsonArray subTasks = jiraTicketResult
                            .getAsJsonObject(FIELDS)
                            .getAsJsonArray(SUBTASKS);
                    for (JsonElement subTask: subTasks) {
                        String subTaskLabel = subTask.getAsJsonObject().get(FIELDS).getAsJsonObject().get(SUMMARY).getAsString();
                        if (subTaskLabel.equals(alphaVoBo)) {
                            String statusSubtask = subTask.getAsJsonObject().getAsJsonObject(FIELDS).getAsJsonObject(STATUS).get(NAME).getAsString();
                            if (statusSubtask.equals(ACCEPTED)) {
                                message = MSG_UUAA+String.join(", ", matchedUuaas)+" bajo dominio de Alpha y Subtarea en estado Accepted";
                                isValid = true;
                                break;
                            }else{
                                message = MSG_UUAA+String.join(", ", matchedUuaas)+" bajo dominio de Alpha y Subtarea en estado incorrecto "+statusSubtask;
                                break;
                            }
                        }
                        else{
                            message = MSG_UUAA+String.join(", ", matchedUuaas)+" bajo dominio de Alpha sin Subtarea";
                        }
                    }
                }
            }
            else {
                message = "No se pudo Validar Alpha por no tener adjuntos.";
            }

        } else {
            message = MSG_RULE_INVALID;
            isValid = true;
        }
        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationItemType(String helpMessage, String group) {
        String message = "";
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

    public Map<String, Object> getValidationDependencyComment(String teamBackLogId, List<String> teamBackLogTicketIdRLB, JiraValidatorByUrlRequest dto, String helpMessage, String group, List<InfoJiraProject> infoJiraProjectList) {
        boolean isValid = true;
        String message = "";
        boolean isWarning = false;
        List<String> rolIdQE = new ArrayList<>(List.of("11","12")); //QE Y QE TEMPORAL
        String isChildPadName;
        JsonArray issueLinks = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonArray(ISSUELINKS);
        if (issueLinks == null || issueLinks.isEmpty()){
            isValid = false;
            message = MSG_RULE_NODEPENDENCY;
            return buildValidationResult(message, isValid, isWarning, helpMessage, group);
        }
        else {
            if(teamBackLogTicketIdRLB.contains(teamBackLogId)){
                message = "Esta regla no es válida para RLB.";
                return buildValidationResult(message, isValid, isWarning, helpMessage, group);
            }
            List<String> isChildPadNameCollection = new ArrayList<>();
            for (JsonElement issueLinkElement : issueLinks) {
                String type  = issueLinkElement.getAsJsonObject().getAsJsonObject(TYPE).get(INWARD).getAsString();
                JsonElement inwardIssue = issueLinkElement
                        .getAsJsonObject()
                        .getAsJsonObject(INWARD_ISSUE);
                if(type.equals(IS_CHILD_ITEM_OF) && inwardIssue != null) {
                    String issuetype = inwardIssue.getAsJsonObject().getAsJsonObject(FIELDS).getAsJsonObject(ISSUETYPE).get(NAME).getAsString();
                    if(issuetype.equalsIgnoreCase(DEPENDENCY)) {
                        isChildPadName = issueLinkElement
                                .getAsJsonObject()
                                .getAsJsonObject(INWARD_ISSUE)
                                .get("key").getAsString();
                        isChildPadNameCollection.add(isChildPadName);
                    }
                }
            }

            if (isChildPadNameCollection.isEmpty()){
                isValid = false;
                message = "Ticket no cuenta con Dependencia Asociada de Type \"Dependency\" o su asociación no es \"is child item of\".";
                return buildValidationResult(message, isValid, isWarning, helpMessage, group);
            }

            try {
                for (String isChildPad : isChildPadNameCollection) {
                    var query = KEY_IN + isChildPad + ")";
                    var url = ApiJiraName.URL_API_JIRA_SQL + query + new JiraApiService().getQuerySuffixURL();
                    var response = new JiraApiService().GetJiraAsync(dto.getUserName(), dto.getToken(), url);
                    JsonObject metaData = JsonParser.parseString(response).getAsJsonObject();

                    JsonArray comments = metaData
                            .getAsJsonArray(ISSUES)
                            .get(0).getAsJsonObject()
                            .getAsJsonObject(FIELDS)
                            .getAsJsonObject("comment")
                            .getAsJsonArray("comments");
                    if (comments.isEmpty()){
                        isValid = false;
                        message = "Dependencia Asociada no tiene comentarios";
                        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
                    }
                    for (JsonElement comment : comments){
                        String authorEmailAddress = comment.getAsJsonObject()
                                .getAsJsonObject("author").get(EMAIL_ADDRESS).getAsString();
                        String comentario = comment.getAsJsonObject()
                                .get("body").getAsString();
                        String patron = "comprometid";
                        Pattern pattern = Pattern.compile(patron, Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(comentario);
                        if (matcher.find()){
                            List<InfoJiraProject> infoJiraProjectListFiltered = infoJiraProjectList.stream().filter(project
                                    -> project.getTeamBackLogId().equals(teamBackLogId)
                                    && project.getParticipantEmail().equals(authorEmailAddress)
                                    && rolIdQE.contains(project.getProjectRolType())
                            ).toList();

                            List<String> personsRolIdQE = infoJiraProjectList.stream()
                                    .filter(project ->  rolIdQE.contains(project.getProjectRolType()))
                                    .map(InfoJiraProject::getParticipantEmail)
                                    .distinct()
                                    .toList();

                            if(infoJiraProjectListFiltered.isEmpty()){
                                if (personsRolIdQE.contains(authorEmailAddress)){
                                    isWarning = true;
                                    message = "Dependencia cuenta con comentario \"Comprometido\", QE o del QE temporal "+authorEmailAddress+" pero no está asociado al proyecto";
                                }
                                else {
                                    isWarning = true;
                                    message = "Dependencia cuenta con comentario \"Comprometido\", pero no de algún QE o del QE temporal";
                                }
                            }
                            else {
                                message = "Dependencia cuenta con comentario \"Comprometido\", del QE o del QE temporal "+ authorEmailAddress+" asociado al proyecto";
                            }
                            break;
                        }
                        else {
                            isValid = false;
                            message = "Dependencia no cuenta con ningun comentario \"Comprometido\"";
                        }

                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return buildValidationResult(message, isValid, isWarning, helpMessage, group);
    }

}
