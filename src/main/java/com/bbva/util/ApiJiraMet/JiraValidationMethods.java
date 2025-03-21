package com.bbva.util.ApiJiraMet;

import com.bbva.common.jiraValidador.JiraValidatorConstantes;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.bbva.common.jiraValidador.JiraValidatorConstantes.*;

public class JiraValidationMethods {
    private final String jiraCode;
    private final List<String> validPAD = Arrays.asList("pad3", "pad5");
    private final JsonObject jiraTicketResult;
    private boolean isInTableroDQA;
    private final boolean isEnviadoFormulario;
    private final String featureLink;
    private final List<String> impactLabel;
    private final String currentQ;
    private final String teamBackLogId;
    private final String teamBackLogDQAId = "2461905";

    public JiraValidationMethods(String jiraCode, JsonObject jiraTicketResult) throws ParseException {
        this.jiraCode = jiraCode;
        this.jiraTicketResult = jiraTicketResult;
        this.isInTableroDQA = false;
        this.isEnviadoFormulario = false;
        JsonElement featureLinkElement = this.jiraTicketResult.get(FIELDS).getAsJsonObject().get(CUSTOMFIELD_10004);
        this.featureLink = featureLinkElement.isJsonNull() ? null : featureLinkElement.getAsString();
        JsonElement impactLabelElement = this.jiraTicketResult.get(FIELDS).getAsJsonObject().get("customfield_10267");
        this.impactLabel = convertJsonElementToList(impactLabelElement);
        this.teamBackLogId = getTeamBackLogId();
        this.currentQ = getCurrentQ();
    }

    public String getCurrentQ(){
        return InfoJiraProjectDao.getInstance().currentQ();
    }

    public String getTeamBackLogId() throws ParseException {
        String teamBackLogId = null;
        Date oldestDate = new SimpleDateFormat("yyyy-MM-dd").parse("9999-12-31");
        JsonArray changelog = jiraTicketResult
                .getAsJsonObject(CHANGELOG)
                .getAsJsonArray(HISTORIES);
        for (JsonElement history : changelog) {
            JsonObject historyObj = history.getAsJsonObject();
            String created = historyObj.get(CREATED).getAsString();
            Date createdDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(created);
            if (createdDate.before(oldestDate)) {
                JsonArray items = historyObj.getAsJsonArray(ITEMS);
                String field = items.get(0).getAsJsonObject().get(FIELD).getAsString();
                if (field.equals(TEAM_BACKLOG)) {
                    if (items.get(0).getAsJsonObject().get("to").getAsString().equals(teamBackLogDQAId)) { //tablero de QA
                        teamBackLogId =  items.get(0).getAsJsonObject().get("from").getAsString();
                        oldestDate = createdDate;
                    }
                }
            }
        }
        if (teamBackLogId == null){
            teamBackLogId = jiraTicketResult.getAsJsonObject(FIELDS)
                    .getAsJsonArray("customfield_13300").get(0).getAsString();
        }
        return teamBackLogId;
    }

    private List<String> convertJsonElementToList(JsonElement element) {
        if (element != null && !element.isJsonNull() && element.isJsonArray()) {
            JsonArray jsonArray = element.getAsJsonArray();
            List<String> resultList = new ArrayList<>();
            jsonArray.forEach(jsonElem -> resultList.add(jsonElem.getAsString()));
            return resultList;
        } else {
            return null;
        }
    }

    public Map<String, Object> getValidationURLJIRA(String helpMessage, String group) {
        String message;
        boolean isValid;
        boolean isWarning = false;

        String[] jiraCodeParts = this.jiraCode.split("-");
        String jiraPADCode = jiraCodeParts[0].toUpperCase();
        jiraPADCode = jiraPADCode.toLowerCase();
        System.out.println(jiraPADCode);
        if (jiraPADCode.contains(this.validPAD.get(0)) || jiraPADCode.contains(this.validPAD.get(1))) {
            message = "Se encontró " + jiraPADCode;
            isValid = true;
        } else {
            message = "No se encontró " + String.join(" o ", this.validPAD);
            isValid = false;
        }

        return Map.of(
                MESSAGE, message,
                ISVALID, isValid,
                ISWARNING, isWarning,
                HELPMESSAGE, helpMessage,
                GROUP, group);
    }

    public Map<String, Object> getValidatorValidateSummaryHUTType(String helpMessage, String group) {
        String message;
        boolean isValid;
        boolean isWarning = false;
        String tipoDesarrolloSummary = "";
        String summaryComparacion = jiraTicketResult.get(FIELDS).getAsJsonObject().get(SUMMARY).toString().toLowerCase();

        for (Map.Entry<String, List<String>> entry : JiraValidatorConstantes.DEVELOPS_TYPES.entrySet()) {
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
        var issueType = jiraTicketResult.getAsJsonObject(FIELDS).getAsJsonObject(ISSUETYPE).get("name").getAsString();

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
        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidatorDocumentAttachByDevType(String tipoDesarrollo) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        var result = JiraValidatorConstantes.ATTACHS_BY_DEVELOP_TYPES.get(tipoDesarrollo);

        var attachments = jiraTicketResult.getAsJsonObject(FIELDS).getAsJsonObject().get(ATTACHMENT).getAsJsonArray();
        attachments.forEach(attachment -> {

        });
        return getValidatonResultsDict(message, isValid, isWarning, HELPMESSAGE, GROUP);
    }

    public Map<String, Object> getValidatorValidateHUTType(List<String> teamBackLogTicketIdRLB, String helpMessage, String tipoDesarrollo, String group) {
        String message;
        boolean isValid;
        boolean isWarning = false;

        JsonArray issuelinks = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonArray(ISSUELINKS);

        String name = null;
        String statusCategory = null;

        String summary = jiraTicketResult.get(FIELDS).getAsJsonObject().get(SUMMARY).toString().replaceAll("^.*?\\[(.*?)\\].*$", "$1").trim();

        if (tipoDesarrollo.equalsIgnoreCase("ingesta")) {
            if(teamBackLogTicketIdRLB.contains(teamBackLogId)){
                message = MSG_RULE_INVALID;
                isValid = true;
            } else {
                for (JsonElement issueLinkElement : issuelinks) {
                    JsonObject issueLink = issueLinkElement.getAsJsonObject();
                    String inward = issueLink.getAsJsonObject("type").get(INWARD).getAsString();
                    if (inward.equalsIgnoreCase("is child item")) {
                        if (issueLink.has(INWARD_ISSUE)) {
                            JsonObject inwardIssue = issueLink.getAsJsonObject(INWARD_ISSUE);
                            if (inwardIssue.has(FIELDS)) {
                                JsonObject fields = inwardIssue.getAsJsonObject(FIELDS);
                                if (fields.has(ISSUETYPE) && fields.has(STATUS)) {
                                    JsonObject issuetype = fields.getAsJsonObject(ISSUETYPE);
                                    JsonObject status = fields.getAsJsonObject(STATUS);
                                    statusCategory = status.get("name").getAsString();
                                    name = issuetype.get("name").getAsString();
                                }
                            }
                        }
                    } else {
                        message = "No es ticket de integración";
                        isValid = true;
                    }
                }
                if (name != null && name.equals(STORY)) {
                    if (statusCategory != null && statusCategory.equals(DEPLOYED)) {
                        message = "Ticket de integración con tickets deployados";
                        isValid = true;
                    } else {
                        message = "Ticket de integración sin tickets deployados";
                        isValid = false;
                    }
                } else {
                    message = "No es ticket de integración";
                    isValid = true;
                }
            }
        } else {
            isValid = true;
            message = MSG_RULE_INVALID;
        }

        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public String multiReplace(String text, Map<String, String> replacements) {
        if (text == null) {
            text = "";
        }
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }
        return text;
    }

    private Map<String, Object> getValidationResultsDict(String message, boolean isValid, boolean isWarning, String helpMessage, String group) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(MESSAGE, message);
        resultMap.put(ISVALID, isValid);
        resultMap.put(ISWARNING, isWarning);
        resultMap.put(HELPMESSAGE, helpMessage);
        resultMap.put(GROUP, group);
        return resultMap;
    }
    private Map<String, Object> getValidatonResultsDict(String message, boolean isValid, boolean isWarning, String helpMessage, String group) {
        Map<String, Object> result = getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
        return getNewMessage(result);
    }
    private Map<String, Object> getNewMessage(Map<String, Object> result) {
        Map<String, Object> newMessage = new HashMap<>();
        newMessage.put(MESSAGE, result.get(MESSAGE));
        newMessage.put(HELPMESSAGE, result.get(HELPMESSAGE));
        newMessage.put(ISVALID, result.get(ISVALID));
        newMessage.put(ISWARNING, result.get(ISWARNING));
        newMessage.put(GROUP, result.get(GROUP));
        return newMessage;
    }

public Map<String, Object> getValidationPR(String tipoDesarrollo, String helpMessage, String group) {
    String message = "";
    boolean isValid = false;
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
    return this.getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
}
    public Map<String, Object> getValidationPRBranch(String tipoDesarrollo, String helpMessage, String group) {
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
                        return this.getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
                    }
                    else{
                        isValid = false;
                        message = "No se encontró PR branch Asociada correcta: "+branch+ ". Solo son validas"+String.join(",",validBranches);
                    }
                }
            }
            else {
                isValid = false;
                message = "No se encontraron PRs asociadas";
            }
        }

        return this.getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
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
                if (statusSubTask.contains(result.toString())){
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
                        .get("name").getAsString();
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

        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationValidateSubtaskPerson(JiraValidatorByUrlRequest dto,String tipoDesarrollo, String helpMessage, String group, List<InfoJiraProject> infoJiraProjectList) {
        String message="Todas Las subtareas tienen el VoBo de la persona asociada al proyecto";
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
        if(subTasks.size()==0){
            message = "HU no cuenta con subtareas asociadas";
            return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
        }

        if(teamBackLogId == null || teamBackLogId.isEmpty()){
            message = "HU sin Team BackLog";
            return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
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
                        .getAsJsonPrimitive("name")
                        .getAsString();
                if(subtaskStatus.equals(DISCARDED)){
                    continue;
                }
                String codeJiraSubTask = subtask.getAsJsonObject().get("key").getAsString();
                var tickets = List.of(codeJiraSubTask);
                var query = KEY_IN + String.join(",", tickets) + ")";

                JsonObject metaData = null;
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
                        ).collect(Collectors.toList());
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

        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
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
                    .getAsJsonPrimitive("name")
                    .getAsString();
            if(subtaskStatus.equals(DISCARDED)){
                continue;
            }
            String codeJiraSubTask = subtask.getAsJsonObject().get("key").getAsString();
            var query = KEY_IN + String.join(",", codeJiraSubTask) + ")";
            JsonObject metaData = null;
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
        return getValidatonResultsDict(message.get(), isValid.get(), isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationAcceptanceCriteria(JiraValidatorByUrlRequest dto, List<String> teamBackLogTicketIdRLB,String tipoDesarrollo, String helpMessage, String group, List<InfoJiraProject> infoJiraProjectList) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;
        Map<String, Object> validAcceptanceCriteriaObject = CRITERIA_BY_DEVELOP_TYPES.get(tipoDesarrollo);

        String acceptanceCriteria = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .get("customfield_10260").getAsString();

        acceptanceCriteria = acceptanceCriteria.replaceAll("[\\s\\u00A0]+", " ").trim();
        if(tipoDesarrollo.equalsIgnoreCase(MALLAS)){ //PR DE TIPO MALLAS
            String teamBackLogTicketId = jiraTicketResult
                    .getAsJsonObject()
                    .getAsJsonObject(FIELDS)
                    .get("customfield_13301").getAsString();

            if(teamBackLogTicketIdRLB.contains(teamBackLogTicketId)){ //TICKET RELIABILITY
                if (!acceptanceCriteria.isEmpty()) {
                    if (validAcceptanceCriteriaObject != null) {
                        String FeatureTicketId = jiraTicketResult
                                .getAsJsonObject()
                                .getAsJsonObject(FIELDS)
                                .get(CUSTOMFIELD_10004).getAsString();

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
                            isValid = false;
                        }

                    } else {
                        message = MSG_RULE_TIPODESARROLLO;
                        isValid = false;
                    }
                } else {
                    message = MSG_RULE_CRITEROACEPTACION;
                    isValid = false;
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
                            isValid = false;
                        }

                    } else {
                        message = MSG_RULE_TIPODESARROLLO;
                        isValid = false;
                    }
                } else {
                    message = MSG_RULE_CRITEROACEPTACION;
                    isValid = false;
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
                        isValid = false;
                    }

                } else {
                    message = MSG_RULE_TIPODESARROLLO;
                    isValid = false;
                }
            } else {
                message = MSG_RULE_CRITEROACEPTACION;
                isValid = false;
            }

        }
        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationTeamAssigned(String tipoDesarrollo, boolean validacionEnvioFormulario, String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        String jiraTicketStatus = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonObject(STATUS)
                .get("name").getAsString();

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
                .get("name").getAsString();

        String currentTeamFieldField = (teamFieldLabelByIssueType.containsKey(issueType)) ? teamFieldLabelByIssueType.get(issueType).get(FIELD) : "";
        String currentTeamFieldLabel = (teamFieldLabelByIssueType.containsKey(issueType)) ? teamFieldLabelByIssueType.get(issueType).get(LABEL) : "";

        List<String> estadosExtraMallasHost = Arrays.asList(READY, "Test", READY_TO_VERIFY);
        List<String> statusTableroDQA = new ArrayList<>();
        statusTableroDQA.add(READY);
        statusTableroDQA.add(IN_PROGRESS);
        statusTableroDQA.add("Test");
        statusTableroDQA.add(READY_TO_VERIFY);
        statusTableroDQA.add("Ready To Deploy");
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
                            List<String> statusTableroDQANew = statusTableroDQA;
                            if (statusTableroDQANew.contains( jiraTicketStatus.trim().toLowerCase())) {
                                if (validacionEnvioFormulario) {
                                    message += "Atención: No olvidar que para regresar el ticket a DQA, se debe cambiar el estado del ticket y la Subtarea DQA";

                                    if (tipoDesarrollo.equals(MALLAS) || tipoDesarrollo.equals("HOST")) {
                                        message += String.join(", ", estadosExtraMallasHost);
                                    } else {
                                        message += READY;
                                    }
                                }
                                isValid = false;
                            }
                        }
                    }
                }
            }
        }
        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationValidateJIRAStatus(String tipoDesarrollo, String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        var jiraTicketStatus = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonObject(STATUS)
                .get("name").getAsString();
        List<String> estadosExtraMallasHost = new ArrayList<>(Arrays.asList(READY, "Test", READY_TO_VERIFY));
        List<String> statusTableroDQA = new ArrayList<>(Arrays.asList(
                READY,
                IN_PROGRESS,
                "Test",
                READY_TO_VERIFY,
                "Ready To Deploy",
                DEPLOYED,
                ACCEPTED
        ));

        if (tipoDesarrollo.equals(MALLAS) || tipoDesarrollo.equals("HOST")) {
            statusTableroDQA.addAll(estadosExtraMallasHost);
        }

        List<String> statusList = statusTableroDQA;

        message = String.format("Con estado %s", jiraTicketStatus);

        if (statusList.contains(jiraTicketStatus)) {
            isValid = true;

            List<String> listaEstados = new ArrayList<>(Arrays.asList(READY, DEPLOYED));

            if (!listaEstados.contains(jiraTicketStatus)) {
                if (this.isInTableroDQA && this.isEnviadoFormulario) {
                    isValid = true;
                    isWarning = true;
                    message += " Atención: Es posible que el %s se encuentre en revisión, recordar que el estado inicial de un %s por revisar es Ready";
                } else {
                    isValid = false;
                }
            }
        }
        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationFeatureLink(String helpMessage, String group) {
        Map<String, Object> result = new HashMap<>();
        String message;
        boolean isValid;
        boolean isWarning = false;


        if (featureLink == null || featureLink.isBlank()) {
            message = MSG_RULE_NOFEATURE;
            isValid = false;
        } else {
            message = ApiJiraName.URL_API_BROWSE + featureLink + " asociado correctamente";
            isValid = true;
        }
        result.put(MESSAGE, message);
        result.put(ISVALID, isValid);
        result.put(ISWARNING, isWarning);
        result.put(HELPMESSAGE, helpMessage);
        result.put(GROUP, group);

        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationFeatureLinkPAD3(String helpMessage, String group) {
        Map<String, Object> result = new HashMap<>();
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        if (featureLink == null || featureLink.isBlank()) {
            message = MSG_RULE_NOFEATURE;
            isValid = false;
        } else {

            if (featureLink.startsWith("PAD3-")) {
                message = "Feature Link asociado correctamente: " + featureLink;
                isValid = true;
            } else {
                message = "Feature Link no comienza con 'PAD3-': " + featureLink;
                isValid = false;
            }
        }

        result.put(MESSAGE, message);
        result.put(ISVALID, isValid);
        result.put(ISWARNING, isWarning);
        result.put(HELPMESSAGE, helpMessage);
        result.put(GROUP, group);

        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);

    }

    public Map<String, Object> getValidationFeatureLinkStatus(JiraValidatorByUrlRequest dto, String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;
        JsonObject metaData = null;
        List<String> validStatuses = Arrays.asList(IN_PROGRESS);
        if (featureLink == null || featureLink.isBlank()) {
            message = MSG_RULE_NOFEATURE;
            isValid = false;
        } else {
            var query = KEY_IN + String.join(",", featureLink) + ")";
            try {
                var url = ApiJiraName.URL_API_JIRA_SQL + query + new JiraApiService().getQuerySuffixURL();
                var response = new JiraApiService().GetJiraAsync(dto.getUserName(), dto.getToken(), url);
                metaData = JsonParser.parseString(response).getAsJsonObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            String featureLinkStatus = metaData
                    .getAsJsonArray(ISSUES)
                    .get(0).getAsJsonObject()
                    .getAsJsonObject(FIELDS)
                    .getAsJsonObject(STATUS)
                    .get("name").getAsString();

            if (validStatuses.contains(featureLinkStatus)) {
                message = "Con estado " + featureLinkStatus;
                isValid = true;
            } else {
                message = "Con estado " + featureLinkStatus;
            }
        }
        return this.getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationFeatureLinkProgramIncrement(JiraValidatorByUrlRequest dto, String helpMessage, String group){
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;
        JsonObject metaData = null;
        if (featureLink == null || featureLink.isBlank()) {
            message = MSG_RULE_NOFEATURE;
            isValid = false;
        } else {
            var query = KEY_IN + String.join(",", featureLink) + ")";

            try {
                var url = ApiJiraName.URL_API_JIRA_SQL + query + new JiraApiService().getQuerySuffixURL();
                var response = new JiraApiService().GetJiraAsync(dto.getUserName(), dto.getToken(), url);
                metaData = JsonParser.parseString(response).getAsJsonObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            JsonArray programIncrement = metaData
                    .getAsJsonArray(ISSUES)
                    .get(0).getAsJsonObject()
                    .getAsJsonObject(FIELDS)
                    .get("customfield_10264")
                    .getAsJsonArray();

            String jiraTicketStatus = jiraTicketResult.get(FIELDS).getAsJsonObject().get(STATUS).getAsJsonObject().get("name").getAsString();

            boolean containsCurrentQ = false;
            for (JsonElement element : programIncrement) {
                if (element.getAsString().equals(currentQ)) {
                    containsCurrentQ = true;
                    break;
                }
            }

            if (programIncrement == null) {
                message = "Sin Program Increment";

                String tipoIncidencia = this.jiraTicketResult.get(FIELDS).getAsJsonObject().get(ISSUETYPE).getAsJsonObject().get("name").getAsString();
                isValid = !tipoIncidencia.isEmpty();
                if (isValid) {
                    message = "Sin Program Increment, pero con tipo de incidencia: " + tipoIncidencia;
                    isWarning = true;
                }
            } else {
                message = "Con Program Increment " + programIncrement.toString();
                isValid = true;

                if (!jiraTicketStatus.equals(DEPLOYED)) {
                    if (!containsCurrentQ) {
                        message += " Atención: El Program Increment debe contener al Q actual (En este caso " + currentQ + ") cuando el ticket este en revisión, " + MSG_COORDINATION_MESSAGE;
                        isValid = false;
                    }
                }
            }
        }
        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationFeatureLinkRLB(JiraValidatorByUrlRequest dto,String tipoDesarrollo, String helpMessage, String group){
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;
        List<String> teamBackLogTicketIdRLB = List.of("6037769","6037765","6037905","4403027","7912651","2461914");

        if(tipoDesarrollo.equalsIgnoreCase(MALLAS)||tipoDesarrollo.equalsIgnoreCase("host")){ // PR DE TIPO MALLAS
            String teamBackLogTicketId = jiraTicketResult
                    .getAsJsonObject()
                    .getAsJsonObject(FIELDS)
                    .get("customfield_13301").getAsString();

            if(teamBackLogTicketIdRLB.contains(teamBackLogTicketId)){
                JsonObject metaData = null;
                List<String> validStatuses = Arrays.asList("INC", "PRB", "PB");

                if (featureLink == null || featureLink.isBlank()) {
                    message = MSG_RULE_NOFEATURE;
                    isValid = false;
                } else {
                    var query = KEY_IN + String.join(",", featureLink) + ")";
                    try {
                        var url = ApiJiraName.URL_API_JIRA_SQL + query + new JiraApiService().getQuerySuffixURL();
                        var response = new JiraApiService().GetJiraAsync(dto.getUserName(), dto.getToken(), url);
                        metaData = JsonParser.parseString(response).getAsJsonObject();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    JsonArray featureLinkLabels = metaData
                            .getAsJsonArray(ISSUES)
                            .get(0).getAsJsonObject()
                            .getAsJsonObject(FIELDS)
                            .getAsJsonArray(LABELS);

                    boolean containsValidStatus = false;

                    for (int i = 0; i < featureLinkLabels.size(); i++) {
                        String label = featureLinkLabels.get(i).getAsString();
                        for (String status : validStatuses) {
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
            }else{
                message += "El ticket no es una incidencia o problema.";
                isValid = true;
            }
        }else{
            message = MSG_RULE_INVALID;
            isValid = true;
        }

        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
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

        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationValidateSubTask(String tipoDesarrollo, String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;
        boolean aditionalLabel = false;
        List<String> aditionalSpecialSubtask = List.of(VB_KM, VB_SO);
        List<String> aditionalSpecialLabels = List.of("datioRutaCritica", "JobsHuerfanos");
        JsonArray labels = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonArray(LABELS);

        List<String> requiredSubTasks = JiraValidatorConstantes.SUBTASKS_BY_DEVELOP_TYPES.get(tipoDesarrollo);

        JsonArray subTasks = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonArray(SUBTASKS);

        List<String> foundSubTasks = new ArrayList<>();
        List<String> additionalSubTasks = new ArrayList<>();
        List<String> foundSpecialLabel = new ArrayList<>();
        List<String> foundSpecialSubtasks = new ArrayList<>();

        for (JsonElement label : labels){
            if (aditionalSpecialLabels.contains(label.getAsString())){
                foundSpecialLabel.add(label.getAsString());
            }
        }

        for (JsonElement subTask : subTasks) {
            String subTaskLabel = subTask.getAsJsonObject().get(FIELDS).getAsJsonObject().get(SUMMARY).getAsString();
            if (requiredSubTasks.contains(subTaskLabel)) {
                foundSubTasks.add(subTaskLabel);
            }else {
                additionalSubTasks.add(subTaskLabel);
                if(!foundSpecialLabel.isEmpty() || aditionalSpecialSubtask.contains(subTaskLabel)){
                    foundSpecialSubtasks.add(subTaskLabel);
                }
            }
        }

        if (foundSubTasks.containsAll(requiredSubTasks)) {
            message = "Todas las subtareas requeridas fueron encontradas.";
            isValid = true;
            if (!additionalSubTasks.isEmpty()) {
                message += " Tambien se encontraron subtareas adicionales: " + String.join(", ", additionalSubTasks)+ ". ";
                if (tipoDesarrollo.equals(MALLAS)){
                    if(!foundSpecialLabel.isEmpty() && foundSpecialSubtasks.isEmpty()){
                        message += MSG_RULE_NOSUBTAREA + String.join(", ", aditionalSpecialSubtask);
                        isValid = false;
                    }
                    else if(foundSpecialLabel.isEmpty() && !foundSpecialSubtasks.isEmpty()) {
                        message += "Se recomienda validar las subtareas adicionales: " + String.join(", ", aditionalSpecialSubtask)
                                + " para casos de jobs eliminados, huerfanos, ruta critica, puede estar pendiente el label correspondiente.";
                        isValid = true;
                        isWarning = true;
                    }
                }
                else {
                    if(!foundSpecialLabel.isEmpty() && foundSpecialSubtasks.isEmpty()){
                        message += MSG_RULE_NOSUBTAREA + String.join(", ", aditionalSpecialSubtask);
                        isValid = false;
                    }
                    else if(foundSpecialLabel.isEmpty() && !foundSpecialSubtasks.isEmpty()) {
                        message += MSG_RULE_RECOMENDATIONSUBTAREA + String.join(", ", aditionalSpecialSubtask)
                                + " para casos de jobs ruta critica, puede estar pendiente el label correspondiente.";
                        isValid = true;
                        isWarning = true;
                    }
                }
            }
        } else {
            List<String> missingSubTasks = new ArrayList<>(requiredSubTasks);
            missingSubTasks.removeAll(foundSubTasks);

            message = "Faltan las siguientes subtareas: " + String.join(", ", missingSubTasks);
            if (!additionalSubTasks.isEmpty()) {
                message += " Además, se encontraron subtareas adicionales: " + String.join(", ", additionalSubTasks);
                if (tipoDesarrollo.equals(MALLAS)){
                    if(!foundSpecialLabel.isEmpty() && foundSpecialSubtasks.isEmpty()){
                        message += MSG_RULE_NOSUBTAREA + String.join(", ", aditionalSpecialSubtask);
                        isValid = false;
                    }
                    else if(foundSpecialLabel.isEmpty() && !foundSpecialSubtasks.isEmpty()) {
                        message += MSG_RULE_RECOMENDATIONSUBTAREA + String.join(", ", aditionalSpecialSubtask)
                                + " para casos de jobs eliminados, huerfanos, ruta critica, puede estar pendiente el label correspondiente.";
                        isValid = true;
                        isWarning = true;
                    }
                }
                else {
                    if(!foundSpecialLabel.isEmpty() && foundSpecialSubtasks.isEmpty()){
                        message += MSG_RULE_NOSUBTAREA + String.join(", ", aditionalSpecialSubtask);
                        isValid = false;
                    }
                    else if(foundSpecialLabel.isEmpty() && !foundSpecialSubtasks.isEmpty()) {
                        message += MSG_RULE_RECOMENDATIONSUBTAREA + String.join(", ", aditionalSpecialSubtask)
                                + " para casos de jobs ruta critica, puede estar pendiente el label correspondiente.";
                        isValid = true;
                        isWarning = true;
                    }
                }
            }
        }

        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationValidateImpactLabel(String helpMessage, String group, String tipoDesarrollo) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;
        if (tipoDesarrollo.equals("HOST") || tipoDesarrollo.equals(MALLAS)) {
            List<String> validImpactLabel = Arrays.asList("AppsInternos", "Datio");
            List<String> validImpactLabelListHost = Arrays.asList("DataHub", "Host", "Plataforma_InformacionalP11");

            List<String> validImpactLabelFinalList = tipoDesarrollo.equals("HOST") ? validImpactLabelListHost : validImpactLabel;

            List<String> impactLabelNotExistsList = new ArrayList<>();


            List<String> jiraTicketImpactLabelList = impactLabel == null ? new ArrayList<>() : this.impactLabel;


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
                isValid = false;
            }
        }
        else{
            message = MSG_RULE_INVALID;
            isValid = true;
        }

        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
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
                    String fixVersionName = fixVersion.getAsJsonObject().get("name").getAsString();
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

        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationValidateAttachment(String tipoDesarrollo, String helpMessage, String group) {
        String message = "";
        boolean isValid;
        boolean isWarning = false;
        List<String> requiredAttachments = JiraValidatorConstantes.ATTACHS_BY_DEVELOP_TYPES.get(tipoDesarrollo.toLowerCase());
        JsonArray attachments = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonArray(ATTACHMENT);
        List<String> foundAttachments = new ArrayList<>();

        if(tipoDesarrollo.equals("productivizacion")){
            message = "Esta regla no es válida para este tipo de desarrollo";
            isValid = true;
            return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
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
            return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
        } else {
            List<String> missingAttachments = new ArrayList<>(requiredAttachments);
            missingAttachments.removeAll(foundAttachments);
            message = "Faltan los siguientes adjuntos: " + String.join(", ", missingAttachments);
            isValid = false;
            return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
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
               String type  = issueLinkElement.getAsJsonObject().getAsJsonObject("type").get(INWARD).getAsString();
               JsonElement inwardIssue = issueLinkElement
                       .getAsJsonObject()
                       .getAsJsonObject(INWARD_ISSUE);
               if (type.equals(IS_CHILD_ITEM_OF) && inwardIssue != null) {
                   name = inwardIssue
                           .getAsJsonObject()
                           .getAsJsonObject(FIELDS).getAsJsonObject(ISSUETYPE).get("name").getAsString();
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
                           .get("name").getAsString().toLowerCase();
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

       return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
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
            return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
        } else {
            List<String> missingLabels = new ArrayList<>(requiredLabels);
            missingLabels.removeAll(foundLabels);
            message = "Faltan las siguientes etiquetas: " + String.join(", ", missingLabels);
            isValid = false;
            return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
        }
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
        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationDependency(List<String> teamBackLogTicketIdRLB,String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        String statusDependencyTicket = "";

        JsonArray issueLinks = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonArray(ISSUELINKS);

        if(teamBackLogTicketIdRLB.contains(teamBackLogId)){
            message = "Proviene del tabero RLB, por lo tanto, no tiene una dependencia asociada.";
            isValid = true;
        } else {
            if (issueLinks == null || issueLinks.isEmpty()) {
                message = MSG_RULE_NODEPENDENCY;
                return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
            } else {
                List<String> statusDependencyCollection = new ArrayList<>();
                List<String> dependencyPadCollection = new ArrayList<>();
                for (JsonElement issueLinkElement : issueLinks) {
                    String type = issueLinkElement.getAsJsonObject().getAsJsonObject("type").get(INWARD).getAsString();
                    JsonElement inwardIssue = issueLinkElement
                            .getAsJsonObject()
                            .getAsJsonObject(INWARD_ISSUE);
                    if (type.equals(IS_CHILD_ITEM_OF) && inwardIssue != null) {
                        String issuetype = inwardIssue.getAsJsonObject().getAsJsonObject(FIELDS).getAsJsonObject(ISSUETYPE).get("name").getAsString();
                        if (issuetype.equalsIgnoreCase(DEPENDENCY)) {
                            statusDependencyTicket = inwardIssue.getAsJsonObject()
                                    .getAsJsonObject(FIELDS)
                                    .getAsJsonObject(STATUS)
                                    .get("name").getAsString().toLowerCase();
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
                    isValid = false;
                    message = MSG_RULE_NODEPENDENCY;
                    return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
                }

                boolean allInProgress = statusDependencyCollection.stream().allMatch(status -> status.equalsIgnoreCase(IN_PROGRESS));

                if (allInProgress) {
                    isValid = true;
                    message = "Todas las dependencias se encuentran en el estado que corresponde.";
                } else {
                    isValid = false;
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
        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationDependencyFeatureVsHUTFeature(List<String> teamBackLogTicketIdRLB, JiraValidatorByUrlRequest dto, String helpMessage, String group, List<InfoJiraProject> infoJiraProjectList) {
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
                return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
            } else {
                List<String> isChildPadNameCollection = new ArrayList<>();
                for (JsonElement issueLinkElement : issueLinks) {
                    String type = issueLinkElement.getAsJsonObject().getAsJsonObject("type").get(INWARD).getAsString();
                    JsonElement inwardIssue = issueLinkElement
                            .getAsJsonObject()
                            .getAsJsonObject(INWARD_ISSUE);
                    if (type.equals(IS_CHILD_ITEM_OF) && inwardIssue != null) {
                        String issuetype = inwardIssue.getAsJsonObject().getAsJsonObject(FIELDS).getAsJsonObject(ISSUETYPE).get("name").getAsString();
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
                        if (!isChildFeatureLink.equals(featureLink)) {
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
        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
    }


    public Map<String, Object> getValidationBoardProject(JiraValidatorByUrlRequest dto, String helpMessage, String feature_link, String group,List<InfoJiraProject> infoJiraProjectList) {
        AtomicReference<String> message = new AtomicReference<>("El tablero es valido");
        AtomicBoolean isValid = new AtomicBoolean(true);
        boolean isWarning = false;
        String summaryTicket =  jiraTicketResult.getAsJsonObject()
                .getAsJsonObject(FIELDS)
                .get(SUMMARY).getAsString();
        var query = KEY_IN + String.join(",", featureLink) + ")";
        JsonObject metaData = null;
        try {
            var url = ApiJiraName.URL_API_JIRA_SQL + query + new JiraApiService().getQuerySuffixURL();
            var response = new JiraApiService().GetJiraAsync(dto.getUserName(), dto.getToken(), url);
            metaData = JsonParser.parseString(response).getAsJsonObject();
        } catch (Exception e) {
            System.out.println("Se produjo un error al validar el proyecto: " + e.getMessage());
        }
        if (metaData != null) {
            String teamBackLogFeatureId = metaData
                    .getAsJsonArray(ISSUES)
                    .get(0).getAsJsonObject()
                    .getAsJsonObject(FIELDS)
                    .getAsJsonArray("customfield_13300").get(0).getAsString();
            if (!teamBackLogFeatureId.equals(teamBackLogId)){
                message.set("El tablero del Ticket es distinto al tablero del Feature");
                isValid.set(false);
            }
        }
        List<InfoJiraProject> projectFiltrado = infoJiraProjectList.stream().filter(project -> project.getTeamBackLogId() != null
                && project.getTeamBackLogId().equals(teamBackLogId)).collect(Collectors.toList());
        if (!projectFiltrado.isEmpty()) {
            String tableroNombre = projectFiltrado.get(0).getTeamBackLogName().trim();
            if (!summaryTicket.toLowerCase().contains(tableroNombre.toLowerCase())) {
                message.set("El tablero del Ticket es distinto al mencionado en el summary");
                isWarning = true;
                isValid.set(false);
            }
        }
        else {
            message.set("El tablero no se ha encontrado como válido para los proyectos habilitados del Q");
            isValid.set(false);
        }
        return getValidationResultsDict(message.get(), isValid.get(), isWarning, helpMessage, group);
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
        List<String> matchedUuaas = new ArrayList<>();
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
                            String statusSubtask = subTask.getAsJsonObject().getAsJsonObject(FIELDS).getAsJsonObject(STATUS).get("name").getAsString();
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
        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
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

        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationTechStack(String helpMessage, String group) {
        String message = "";
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
        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationDependencyComment(List<String> teamBackLogTicketIdRLB, JiraValidatorByUrlRequest dto, String helpMessage, String group, List<InfoJiraProject> infoJiraProjectList) {
        boolean isValid = true;
        String message = "";
        boolean isWarning = false;
        List<String> rolIdQE = new ArrayList<>(List.of("11","12")); //QE Y QE TEMPORAL
        String isChildPadName ="";
        JsonArray issueLinks = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonArray(ISSUELINKS);
        if (issueLinks == null || issueLinks.isEmpty()){
            isValid = false;
            message = MSG_RULE_NODEPENDENCY;
            return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
        }
        else {
            if(teamBackLogTicketIdRLB.contains(teamBackLogId)){
                message = "Esta regla no es válida para RLB.";
                return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
            }
            List<String> isChildPadNameCollection = new ArrayList<>();
            for (JsonElement issueLinkElement : issueLinks) {
                String type  = issueLinkElement.getAsJsonObject().getAsJsonObject("type").get(INWARD).getAsString();
                JsonElement inwardIssue = issueLinkElement
                        .getAsJsonObject()
                        .getAsJsonObject(INWARD_ISSUE);
                if(type.equals(IS_CHILD_ITEM_OF) && inwardIssue != null) {
                    String issuetype = inwardIssue.getAsJsonObject().getAsJsonObject(FIELDS).getAsJsonObject(ISSUETYPE).get("name").getAsString();
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
                return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
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
                        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
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
        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
    }
}
