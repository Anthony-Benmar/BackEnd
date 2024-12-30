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
    private String jiraCode;
    private final List<String> validPAD = Arrays.asList("pad3", "pad5");
    private JsonObject jiraTicketResult;
    private boolean isInTableroDQA;
    private boolean isEnviadoFormulario;
    private String featureLink;
    private List<String> impactLabel;
    private String coordinationMessage = "de ser necesario coordinar con el <strong>SM / QE</strong>";
    private String currentQ;
    private String teamBackLogId;
    private final String teamBackLogDQAId = "2461905";

    public JiraValidationMethods(String jiraCode, JsonObject jiraTicketResult) throws ParseException {
        this.jiraCode = jiraCode;
        this.jiraTicketResult = jiraTicketResult;
        this.isInTableroDQA = false;
        this.isEnviadoFormulario = false;
        JsonElement featureLinkElement = this.jiraTicketResult.get("fields").getAsJsonObject().get("customfield_10004");
        this.featureLink = featureLinkElement.isJsonNull() ? null : featureLinkElement.getAsString();
        JsonElement impactLabelElement = this.jiraTicketResult.get("fields").getAsJsonObject().get("customfield_10267");
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
                .getAsJsonObject("changelog")
                .getAsJsonArray("histories");
        for (JsonElement history : changelog) {
            JsonObject historyObj = history.getAsJsonObject();
            String created = historyObj.get("created").getAsString();
            Date createdDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(created);
            if (createdDate.before(oldestDate)) {
                JsonArray items = historyObj.getAsJsonArray("items");
                String field = items.get(0).getAsJsonObject().get("field").getAsString();
                if (field.equals("Team Backlog")) {
                    if (items.get(0).getAsJsonObject().get("to").getAsString().equals(teamBackLogDQAId)) { //tablero de QA
                        teamBackLogId =  items.get(0).getAsJsonObject().get("from").getAsString();
                        oldestDate = createdDate;
                    }
                }
            }
        }
        if (teamBackLogId == null){
            teamBackLogId = jiraTicketResult.getAsJsonObject("fields")
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
                "message", message,
                "isValid", isValid,
                "isWarning", isWarning,
                "helpMessage", helpMessage,
                "group", group);
    }

    public Map<String, Object> getValidatorValidateSummaryHUTType(String helpMessage, String group) {
        String message;
        boolean isValid;
        boolean isWarning = false;
        String tipoDesarrolloSummary = "";
        String summaryComparacion = jiraTicketResult.get("fields").getAsJsonObject().get("summary").toString().toLowerCase();

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
                "message", message,
                "isValid", isValid,
                "isWarning", isWarning,
                "helpMessage", helpMessage,
                "group", group,
                "tipoDesarrolloSummary", tipoDesarrolloSummary
        );
    }

    public Map<String, Object> getValidatorIssueType(String tipoDesarrollo,String helpMessage, String group) {
        var issueType = jiraTicketResult.getAsJsonObject("fields").getAsJsonObject("issuetype").get("name").getAsString();

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

        var attachments = jiraTicketResult.getAsJsonObject("fields").getAsJsonObject().get("attachment").getAsJsonArray();
        attachments.forEach(attachment -> {

        });
        return getValidatonResultsDict(message, isValid, isWarning, "helpMessage", "group");
    }

    public Map<String, Object> getValidatorValidateHUTType(List<String> teamBackLogTicketIdRLB, String helpMessage, String tipoDesarrollo, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        JsonArray issuelinks = jiraTicketResult
                .getAsJsonObject("fields")
                .getAsJsonArray("issuelinks");

        String name = null;
        String statusCategory = null;

        String summary = jiraTicketResult.get("fields").getAsJsonObject().get("summary").toString().replaceAll("^.*?\\[(.*?)\\].*$", "$1").trim();

        if (tipoDesarrollo.equalsIgnoreCase("ingesta")) {
            if(teamBackLogTicketIdRLB.contains(teamBackLogId)){
                message = "Esta regla no es válida para este tipo de desarrollo.";
                isValid = true;
            } else {
                for (JsonElement issueLinkElement : issuelinks) {
                    JsonObject issueLink = issueLinkElement.getAsJsonObject();
                    String inward = issueLink.getAsJsonObject("type").get("inward").getAsString();
                    if (inward.equalsIgnoreCase("is child item")) {
                        if (issueLink.has("inwardIssue")) {
                            JsonObject inwardIssue = issueLink.getAsJsonObject("inwardIssue");
                            if (inwardIssue.has("fields")) {
                                JsonObject fields = inwardIssue.getAsJsonObject("fields");
                                if (fields.has("issuetype") && fields.has("status")) {
                                    JsonObject issuetype = fields.getAsJsonObject("issuetype");
                                    JsonObject status = fields.getAsJsonObject("status");
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
                if (name != null && name.equals("Story")) {
                    if (statusCategory != null && statusCategory.equals("Deployed")) {
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
            message = "Esta regla no es válida para este tipo de desarrollo.";
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
        resultMap.put("message", message);
        resultMap.put("isValid", isValid);
        resultMap.put("isWarning", isWarning);
        resultMap.put("helpMessage", helpMessage);
        resultMap.put("group", group);
        return resultMap;
    }
    private Map<String, Object> getValidatonResultsDict(String message, boolean isValid, boolean isWarning, String helpMessage, String group) {
        Map<String, Object> result = getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
        return getNewMessage(result);
    }
    private Map<String, Object> getNewMessage(Map<String, Object> result) {
        Map<String, Object> newMessage = new HashMap<>();
        newMessage.put("message", result.get("message"));
        newMessage.put("helpMessage", result.get("helpMessage"));
        newMessage.put("isValid", result.get("isValid"));
        newMessage.put("isWarning", result.get("isWarning"));
        newMessage.put("group", result.get("group"));
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
            .getAsJsonObject("fields");

    List<String> tipoDesarrolloPRs = Arrays.asList("Procesamiento","MigrationTool",
            "Hammurabi", "Ingesta", "Scaffolder", "Operativizacion",
            "Teradata", "SmartCleaner","SparkCompactor", "JSON Global");

    tipoDesarrolloPRs.replaceAll(String::toLowerCase);

    if (jiraTicketResultPrs.get("prs") != null) {
        int cantidadPRs = jiraTicketResultPrs.get("prs").getAsJsonArray().size();
        if (cantidadPRs > 0) {
            for (JsonElement prObj : jiraTicketResultPrs.get("prs").getAsJsonArray()) {
                String status = jiraTicketResultPrs.get("prs").getAsJsonArray().get(0).getAsJsonObject().get("status").getAsString();
                if (prsStatusException.contains(status)){
                    prException.put(prObj.getAsJsonObject().get("url").getAsString(), prObj.getAsJsonObject().get("status").getAsString());
                } else if (prsStatusWarning.contains(status)) {
                    prWarning.put(prObj.getAsJsonObject().get("url").getAsString(), prObj.getAsJsonObject().get("status").getAsString());
                }else{
                    prValid.put(prObj.getAsJsonObject().get("url").getAsString(), prObj.getAsJsonObject().get("status").getAsString());
                }
            }
        }
    }
    int cantidadPrsValidas = prValid.size();
    int cantidadPrsWarning = prWarning.size();
    if (tipoDesarrollo.equals("PRs") || tipoDesarrollo.equals("mallas") || tipoDesarrolloPRs.contains(tipoDesarrollo)) {
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
                .getAsJsonObject("fields");

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
        results.addAll(List.of("[VB][KM]", "[VB][SO]"));
        if(tipoDesarrollo.equalsIgnoreCase("mallas") || tipoDesarrollo.equalsIgnoreCase("host")) {
            results.add("[VB][DEV]");
        }
        results.removeIf(subtask -> subtask.contains("QA"));

        List<JsonObject> subTaskCollection = new ArrayList<>();
        JsonArray subTaskCollectionBadStatus = new JsonArray();
        List<String> subTaskLabelBadStatus = new ArrayList<>();

        JsonArray subTasks = jiraTicketResult
                .getAsJsonObject("fields")
                .getAsJsonArray("subtasks");

        subTasks.forEach(subtask -> {
            String statusSubTask =subtask.getAsJsonObject()
                    .getAsJsonObject("fields")
                    .get("summary").getAsString();

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
                        .getAsJsonObject("fields")
                        .getAsJsonObject("status")
                        .get("name").getAsString();
                if (!status.equalsIgnoreCase("Accepted") && !status.equalsIgnoreCase("Discarded")){
                    subTaskCollectionBadStatus.add(subTaskElement);
                    subTaskLabelBadStatus.add(subTaskElement.getAsJsonObject().getAsJsonObject("fields").get("summary").getAsString());
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
        results.addAll(List.of("[VB][KM]", "[VB][SO]"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        JsonArray subTasks = jiraTicketResult
                .getAsJsonObject("fields")
                .getAsJsonArray("subtasks");
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
                        .getAsJsonObject("fields")
                        .get("summary").getAsString();

                if(subtaskLabel.contains("QA") || subtaskLabel.contains("DEV")|| subtaskLabel.contains("GC")){
                    continue;
                }
                String subtaskStatus = subtask.getAsJsonObject()
                        .getAsJsonObject("fields")
                        .getAsJsonObject("status")
                        .getAsJsonPrimitive("name")
                        .getAsString();
                if(subtaskStatus.equals("Discarded")){
                    continue;
                }
                String codeJiraSubTask = subtask.getAsJsonObject().get("key").getAsString();
                var tickets = List.of(codeJiraSubTask);
                var query = "key%20in%20(" + String.join(",", tickets) + ")";

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
                        .getAsJsonArray("issues")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("fields")
                        .getAsJsonObject("assignee")
                        .get("emailAddress").getAsString();
                if (subtaskAsignee == null || subtaskAsignee.isBlank()) {
                    messsageBadList.add("Subtarea "+ subtaskLabel +" sin asignación");
                    continue;
                }
                JsonArray historiesSubstask =
                        metaData.getAsJsonObject().getAsJsonArray("issues")
                                .get(0).getAsJsonObject()
                                .getAsJsonObject("changelog")
                                .getAsJsonArray("histories");
                for (int i = 0; i < historiesSubstask.size(); i++) {
                    JsonObject history = historiesSubstask.get(i).getAsJsonObject();
                    String createdDate = history.get("created").getAsString();
                    LocalDateTime historyDate = LocalDateTime.parse(createdDate, formatter);
                    JsonArray itemsHistory = history.getAsJsonArray("items");

                    if (itemsHistory != null && itemsHistory.size() > 0) {
                        boolean hasMatchingItem = false;

                        for (JsonElement itemElement : itemsHistory) {
                            JsonObject item = itemElement.getAsJsonObject();
                            String toString = item.get("toString").getAsString();
                            if ("Accepted".equals(toString)) {
                                hasMatchingItem = true;
                                break;
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
                    messsageBadList.add("Subtarea "+ subtaskLabel +" no tiene estado Accepted");
                    continue;
                }

                String voboPerson = maxHistory.getAsJsonObject("author").get("emailAddress").getAsString();
                if (!voboPerson.equalsIgnoreCase(subtaskAsignee)){
                    messsageBadList.add("Subtarea "+ subtaskLabel +" VoBo de "+ voboPerson +" no es el mismo asignado en la subtarea");
                    continue;
                }
                List<InfoJiraProject> projectsFiltrados = new ArrayList<>();
                for (Map.Entry<String, Map<String, Object>> entry : SUBTASKS_TYPE_OWNER.entrySet()) {
                    List<String> items = (List<String>) entry.getValue().get("items");
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
        results.addAll(List.of("[VB][KM]", "[VB][SO]","[VB][ALPHA]"));
        JsonArray subTasks = jiraTicketResult
                .getAsJsonObject("fields")
                .getAsJsonArray("subtasks");

        List<String> messsageBadList = new ArrayList<>();
        List<String> messsageGoodList = new ArrayList<>();

        for(JsonElement subtask: subTasks){
            String subtaskLabel = subtask.getAsJsonObject()
                    .getAsJsonObject("fields")
                    .get("summary").getAsString();
            if(subtaskLabel.contains("QA") || subtaskLabel.contains("DEV")){
                continue;
            }
            String subtaskStatus = subtask.getAsJsonObject()
                    .getAsJsonObject("fields")
                    .getAsJsonObject("status")
                    .getAsJsonPrimitive("name")
                    .getAsString();
            if(subtaskStatus.equals("Discarded")){
                continue;
            }
            String codeJiraSubTask = subtask.getAsJsonObject().get("key").getAsString();
            var query = "key%20in%20(" + String.join(",", codeJiraSubTask) + ")";
            JsonObject metaData = null;
            try {
                var url = ApiJiraName.URL_API_JIRA_SQL + query + new JiraApiService().getQuerySuffixURL();
                var response = new JiraApiService().GetJiraAsync(dto.getUserName(),dto.getToken(),url);
                metaData = JsonParser.parseString(response).getAsJsonObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            JsonObject assignee = metaData
                    .getAsJsonArray("issues")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("fields")
                    .getAsJsonObject("assignee");
            String asigneeEmail = assignee.get("emailAddress").getAsString();
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
                .getAsJsonObject("fields")
                .get("customfield_10260").getAsString();

        acceptanceCriteria = acceptanceCriteria.replaceAll("[\\s\\u00A0]+", " ").trim();
        if(tipoDesarrollo.equalsIgnoreCase("mallas")){ //PR DE TIPO MALLAS
            String teamBackLogTicketId = jiraTicketResult
                    .getAsJsonObject()
                    .getAsJsonObject("fields")
                    .get("customfield_13301").getAsString();

            if(teamBackLogTicketIdRLB.contains(teamBackLogTicketId)){ //TICKET RELIABILITY
                if (!acceptanceCriteria.isEmpty()) {
                    if (validAcceptanceCriteriaObject != null) {
                        String FeatureTicketId = jiraTicketResult
                                .getAsJsonObject()
                                .getAsJsonObject("fields")
                                .get("customfield_10004").getAsString();

                        String expectedPattern = (String) validAcceptanceCriteriaObject.get("texto");

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
                            message = String.format("Es válido: %s", acceptanceCriteria);
                            isValid = true;
                        } else {
                            message = "Criterio de aceptación no cumple con el formato requerido";
                            isValid = false;
                        }

                    } else {
                        message = "Tipo de desarrollo no encontrado en los criterios de aceptación";
                        isValid = false;
                    }
                } else {
                    message = "Sin Criterio de Aceptación";
                    isValid = false;
                }
            } else{

                if (!acceptanceCriteria.isEmpty()) {
                    if (validAcceptanceCriteriaObject != null) {
                        String expectedPattern = (String) validAcceptanceCriteriaObject.get("texto");

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
                            message = String.format("Es válido: %s", acceptanceCriteria);
                            isValid = true;
                        } else {
                            message = "Criterio de aceptación no cumple con el formato requerido";
                            isValid = false;
                        }

                    } else {
                        message = "Tipo de desarrollo no encontrado en los criterios de aceptación";
                        isValid = false;
                    }
                } else {
                    message = "Sin Criterio de Aceptación";
                    isValid = false;
                }
            }

        } else{
            if (!acceptanceCriteria.isEmpty()) {
                if (validAcceptanceCriteriaObject != null) {
                    String expectedPattern = (String) validAcceptanceCriteriaObject.get("texto");
                    String[] palabras = expectedPattern.split("\\s+");

                    if (palabras.length >= 11) {
                        message = String.format("Es válido: %s", acceptanceCriteria);
                        isValid = true;
                    } else {
                        message = "Criterio de aceptación no cumple con el formato requerido";
                        isValid = false;
                    }

                } else {
                    message = "Tipo de desarrollo no encontrado en los criterios de aceptación";
                    isValid = false;
                }
            } else {
                message = "Sin Criterio de Aceptación";
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
                .getAsJsonObject("fields")
                .getAsJsonObject("status")
                .get("name").getAsString();

        Map<String, String> storyMap = new HashMap<>();
        storyMap.put("label", "Team Backlog");
        storyMap.put("field", "teamId");
        Map<String, String> dependencyMap = new HashMap<>();
        dependencyMap.put("label", "Receptor Team");
        dependencyMap.put("field", "receptorTeamId");
        Map<String, Map<String, String>> teamFieldLabelByIssueType = new HashMap<>();
        teamFieldLabelByIssueType.put("Historia", storyMap);
        teamFieldLabelByIssueType.put("Story", storyMap);
        teamFieldLabelByIssueType.put("Dependency", storyMap); //dependencyMap
        String issueType = jiraTicketResult.getAsJsonObject("fields")
                .getAsJsonObject("issuetype")
                .get("name").getAsString();

        String currentTeamFieldField = (teamFieldLabelByIssueType.containsKey(issueType)) ? teamFieldLabelByIssueType.get(issueType).get("field") : "";
        String currentTeamFieldLabel = (teamFieldLabelByIssueType.containsKey(issueType)) ? teamFieldLabelByIssueType.get(issueType).get("label") : "";

        List<String> estadosExtraMallasHost = Arrays.asList("Ready", "Test", "Ready To Verify");
        List<String> statusTableroDQA = new ArrayList<>();
        statusTableroDQA.add("Ready");
        statusTableroDQA.add("In Progress");
        statusTableroDQA.add("Test");
        statusTableroDQA.add("Ready To Verify");
        statusTableroDQA.add("Ready To Deploy");
        statusTableroDQA.add("Deployed");
        statusTableroDQA.add("Accepted");
        statusTableroDQA.replaceAll(String::toLowerCase);

        JsonArray histories = this.jiraTicketResult.getAsJsonObject("changelog").getAsJsonArray("histories");

        for (JsonElement historyElement : histories) {
            JsonObject history = historyElement.getAsJsonObject();

            if (history.has("items")) {
                JsonArray items = history.getAsJsonArray("items");
                for (JsonElement itemElement : items) {
                    JsonObject item = itemElement.getAsJsonObject();

                    if (item.has("field") && item.get("field").getAsString().equals(currentTeamFieldLabel)) {
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

                                    if (tipoDesarrollo.equals("mallas") || tipoDesarrollo.equals("HOST")) {
                                        message += String.join(", ", estadosExtraMallasHost);
                                    } else {
                                        message += "Ready";
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
                .getAsJsonObject("fields")
                .getAsJsonObject("status")
                .get("name").getAsString();
        List<String> estadosExtraMallasHost = new ArrayList<>(Arrays.asList("Ready", "Test", "Ready To Verify"));
        List<String> statusTableroDQA = new ArrayList<>(Arrays.asList(
                "Ready",
                "In Progress",
                "Test",
                "Ready To Verify",
                "Ready To Deploy",
                "Deployed",
                "Accepted"
        ));

        if (tipoDesarrollo.equals("mallas") || tipoDesarrollo.equals("HOST")) {
            statusTableroDQA.addAll(estadosExtraMallasHost);
        }

        List<String> statusList = statusTableroDQA;

        message = String.format("Con estado %s", jiraTicketStatus);

        if (statusList.contains(jiraTicketStatus)) {
            isValid = true;

            List<String> listaEstados = new ArrayList<>(Arrays.asList("Ready", "Deployed"));

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
            message = "Sin Feature Link asociado";
            isValid = false;
        } else {
            message = ApiJiraName.URL_API_BROWSE + featureLink + " asociado correctamente";
            isValid = true;
        }
        result.put("message", message);
        result.put("isValid", isValid);
        result.put("isWarning", isWarning);
        result.put("helpMessage", helpMessage);
        result.put("group", group);

        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationFeatureLinkPAD3(String helpMessage, String group) {
        Map<String, Object> result = new HashMap<>();
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        if (featureLink == null || featureLink.isBlank()) {
            message = "Sin Feature Link asociado";
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

        result.put("message", message);
        result.put("isValid", isValid);
        result.put("isWarning", isWarning);
        result.put("helpMessage", helpMessage);
        result.put("group", group);

        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);

    }

    public Map<String, Object> getValidationFeatureLinkStatus(JiraValidatorByUrlRequest dto, String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;
        JsonObject metaData = null;
        List<String> validStatuses = Arrays.asList("In Progress");
        if (featureLink == null || featureLink.isBlank()) {
            message = "Sin Feature Link asociado";
            isValid = false;
        } else {
            var query = "key%20in%20(" + String.join(",", featureLink) + ")";
            try {
                var url = ApiJiraName.URL_API_JIRA_SQL + query + new JiraApiService().getQuerySuffixURL();
                var response = new JiraApiService().GetJiraAsync(dto.getUserName(), dto.getToken(), url);
                metaData = JsonParser.parseString(response).getAsJsonObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            String featureLinkStatus = metaData
                    .getAsJsonArray("issues")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("fields")
                    .getAsJsonObject("status")
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
            message = "Sin Feature Link asociado";
            isValid = false;
        } else {
            var query = "key%20in%20(" + String.join(",", featureLink) + ")";

            try {
                var url = ApiJiraName.URL_API_JIRA_SQL + query + new JiraApiService().getQuerySuffixURL();
                var response = new JiraApiService().GetJiraAsync(dto.getUserName(), dto.getToken(), url);
                metaData = JsonParser.parseString(response).getAsJsonObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            JsonArray programIncrement = metaData
                    .getAsJsonArray("issues")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("fields")
                    .get("customfield_10264")
                    .getAsJsonArray();

            String jiraTicketStatus = jiraTicketResult.get("fields").getAsJsonObject().get("status").getAsJsonObject().get("name").getAsString();

            boolean containsCurrentQ = false;
            for (JsonElement element : programIncrement) {
                if (element.getAsString().equals(currentQ)) {
                    containsCurrentQ = true;
                    break;
                }
            }

            if (programIncrement == null) {
                message = "Sin Program Increment";

                String tipoIncidencia = this.jiraTicketResult.get("fields").getAsJsonObject().get("issuetype").getAsJsonObject().get("name").getAsString();
                isValid = !tipoIncidencia.isEmpty();
                if (isValid) {
                    message = "Sin Program Increment, pero con tipo de incidencia: " + tipoIncidencia;
                    isWarning = true;
                }
            } else {
                message = "Con Program Increment " + programIncrement.toString();
                isValid = true;

                if (!jiraTicketStatus.equals("Deployed")) {
                    if (!containsCurrentQ) {
                        message += " Atención: El Program Increment debe contener al Q actual (En este caso " + currentQ + ") cuando el ticket este en revisión, " + coordinationMessage;
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

        if(tipoDesarrollo.equalsIgnoreCase("mallas")||tipoDesarrollo.equalsIgnoreCase("host")){ // PR DE TIPO MALLAS
            String teamBackLogTicketId = jiraTicketResult
                    .getAsJsonObject()
                    .getAsJsonObject("fields")
                    .get("customfield_13301").getAsString();

            if(teamBackLogTicketIdRLB.contains(teamBackLogTicketId)){
                JsonObject metaData = null;
                List<String> validStatuses = Arrays.asList("INC", "PRB", "PB");

                if (featureLink == null || featureLink.isBlank()) {
                    message = "Sin Feature Link asociado";
                    isValid = false;
                } else {
                    var query = "key%20in%20(" + String.join(",", featureLink) + ")";
                    try {
                        var url = ApiJiraName.URL_API_JIRA_SQL + query + new JiraApiService().getQuerySuffixURL();
                        var response = new JiraApiService().GetJiraAsync(dto.getUserName(), dto.getToken(), url);
                        metaData = JsonParser.parseString(response).getAsJsonObject();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    JsonArray featureLinkLabels = metaData
                            .getAsJsonArray("issues")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("fields")
                            .getAsJsonArray("labels");

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
            message = "Esta regla no es válida para este tipo de desarrollo.";
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
        List<String> aditionalSpecialSubtask = List.of("[VB][KM]", "[VB][SO]");
        List<String> aditionalSpecialLabels = List.of("datioRutaCritica", "JobsHuerfanos");
        JsonArray labels = jiraTicketResult
                .getAsJsonObject("fields")
                .getAsJsonArray("labels");

        List<String> requiredSubTasks = JiraValidatorConstantes.SUBTASKS_BY_DEVELOP_TYPES.get(tipoDesarrollo);

        JsonArray subTasks = jiraTicketResult
                .getAsJsonObject("fields")
                .getAsJsonArray("subtasks");

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
            String subTaskLabel = subTask.getAsJsonObject().get("fields").getAsJsonObject().get("summary").getAsString();
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
                if (tipoDesarrollo.equals("mallas")){
                    if(!foundSpecialLabel.isEmpty() && foundSpecialSubtasks.isEmpty()){
                        message += "Faltan alguna de las siguientes subtareas: " + String.join(", ", aditionalSpecialSubtask);
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
                        message += "Faltan alguna de las siguientes subtareas: " + String.join(", ", aditionalSpecialSubtask);
                        isValid = false;
                    }
                    else if(foundSpecialLabel.isEmpty() && !foundSpecialSubtasks.isEmpty()) {
                        message += "Se recomienda validar las subtareas adicional: " + String.join(", ", aditionalSpecialSubtask)
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
                if (tipoDesarrollo.equals("mallas")){
                    if(!foundSpecialLabel.isEmpty() && foundSpecialSubtasks.isEmpty()){
                        message += "Faltan alguna de las siguientes subtareas: " + String.join(", ", aditionalSpecialSubtask);
                        isValid = false;
                    }
                    else if(foundSpecialLabel.isEmpty() && !foundSpecialSubtasks.isEmpty()) {
                        message += "Se recomienda validar las subtareas adicional: " + String.join(", ", aditionalSpecialSubtask)
                                + " para casos de jobs eliminados, huerfanos, ruta critica, puede estar pendiente el label correspondiente.";
                        isValid = true;
                        isWarning = true;
                    }
                }
                else {
                    if(!foundSpecialLabel.isEmpty() && foundSpecialSubtasks.isEmpty()){
                        message += "Faltan alguna de las siguientes subtareas: " + String.join(", ", aditionalSpecialSubtask);
                        isValid = false;
                    }
                    else if(foundSpecialLabel.isEmpty() && !foundSpecialSubtasks.isEmpty()) {
                        message += "Se recomienda validar las subtareas adicional: " + String.join(", ", aditionalSpecialSubtask)
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
        if (tipoDesarrollo.equals("HOST") || tipoDesarrollo.equals("mallas")) {
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
            message = "Esta regla no es válida para este tipo de desarrollo.";
            isValid = true;
        }

        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationFixVersion(String tipoDesarrollo, String helpMessage, String group) {
        String message = "";
        boolean isValid;
        boolean isWarning = false;
        if (tipoDesarrollo.equals("HOST") || tipoDesarrollo.equals("mallas")) {
            String[] jiraCodeParts = this.jiraCode.split("-");
            String jiraPADCode = jiraCodeParts[0].toUpperCase();

            JsonArray fixVersions = this.jiraTicketResult.getAsJsonObject("fields").getAsJsonArray("fixVersions");

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
                message = "Esta regla no es válida para este tipo de desarrollo.";
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
                .getAsJsonObject("fields")
                .getAsJsonArray("attachment");
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
               .getAsJsonObject("fields")
               .getAsJsonArray("issuelinks");

       String name = null;
       String statusCategory = null;

       JsonArray issueLinkStory = new JsonArray();
       if (tipoDesarrollo.equalsIgnoreCase("productivizacion")) {
           for (JsonElement issueLinkElement : issuelinks) {
               String type  = issueLinkElement.getAsJsonObject().getAsJsonObject("type").get("inward").getAsString();
               JsonElement inwardIssue = issueLinkElement
                       .getAsJsonObject()
                       .getAsJsonObject("inwardIssue");
               if (type.equals("is child item of") && inwardIssue != null) {
                   name = inwardIssue
                           .getAsJsonObject()
                           .getAsJsonObject("fields").getAsJsonObject("issuetype").get("name").getAsString();
                   if(name.equals("Story")){
                       issueLinkStory.add(issueLinkElement);
                   }
               }
           }

           if (!issueLinkStory.isEmpty()) {
               for (JsonElement issueLinkElement : issueLinkStory) {
                   statusCategory = issueLinkElement.getAsJsonObject()
                           .getAsJsonObject("inwardIssue")
                           .getAsJsonObject("fields")
                           .getAsJsonObject("status")
                           .get("name").getAsString().toLowerCase();
                   if (statusCategory.equalsIgnoreCase("Deployed")) {
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
           message = "Esta regla no es válida para este tipo de desarrollo.";
       }

       return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
   }

   public Map<String, Object> getValidationLabels(String tipoDesarrollo, String helpMessage, String group){
        String message = "";
        boolean isValid;
        boolean isWarning = false;
        List<String> requiredLabels = LABELS_BY_DEVELOP_TYPES.get(tipoDesarrollo.toLowerCase());
        JsonArray labels = jiraTicketResult
                .getAsJsonObject("fields")
                .getAsJsonArray("labels");
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
                .getAsJsonObject("changelog")
                .getAsJsonArray("histories");

        Date oldestDate = new SimpleDateFormat("yyyy-MM-dd").parse("9999-12-31");
        String from = "";
        String fromString = "";
        for (JsonElement history : changelog) {
            JsonObject historyObj = history.getAsJsonObject();
            String created = historyObj.get("created").getAsString();
            Date createdDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(created);

            if (createdDate.before(oldestDate)) {
                JsonArray items = historyObj.getAsJsonArray("items");
                String field = items.get(0).getAsJsonObject().get("field").getAsString();
                if (field.equals("Team Backlog")) {
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
                .getAsJsonObject("fields")
                .getAsJsonArray("issuelinks");

        if(teamBackLogTicketIdRLB.contains(teamBackLogId)){
            message = "Proviene del tabero RLB, por lo tanto, no tiene una dependencia asociada.";
            isValid = true;
        } else {
            if (issueLinks == null || issueLinks.isEmpty()) {
                message = "Ticket no cuenta con Dependencia Asociada.";
                return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
            } else {
                List<String> statusDependencyCollection = new ArrayList<>();
                List<String> dependencyPadCollection = new ArrayList<>();
                for (JsonElement issueLinkElement : issueLinks) {
                    String type = issueLinkElement.getAsJsonObject().getAsJsonObject("type").get("inward").getAsString();
                    JsonElement inwardIssue = issueLinkElement
                            .getAsJsonObject()
                            .getAsJsonObject("inwardIssue");
                    if (type.equals("is child item of") && inwardIssue != null) {
                        String issuetype = inwardIssue.getAsJsonObject().getAsJsonObject("fields").getAsJsonObject("issuetype").get("name").getAsString();
                        if (issuetype.equalsIgnoreCase("Dependency")) {
                            statusDependencyTicket = inwardIssue.getAsJsonObject()
                                    .getAsJsonObject("fields")
                                    .getAsJsonObject("status")
                                    .get("name").getAsString().toLowerCase();
                            statusDependencyCollection.add(statusDependencyTicket);
                            dependencyPadCollection
                                    .add(issueLinkElement
                                            .getAsJsonObject()
                                            .getAsJsonObject("inwardIssue")
                                            .get("key").getAsString());
                        }
                    }
                }
                if (statusDependencyCollection.isEmpty()) {
                    isValid = false;
                    message = "Ticket no cuenta con Dependencia Asociada.";
                    return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
                }

                boolean allInProgress = statusDependencyCollection.stream().allMatch(status -> status.equalsIgnoreCase("In Progress"));

                if (allInProgress) {
                    isValid = true;
                    message = "Todas las dependencias se encuentran en el estado que corresponde.";
                } else {
                    isValid = false;
                    isWarning = true;
                    StringBuilder urlMessage = new StringBuilder("Las siguientes dependencias: ");
                    for (int i = 0; i < statusDependencyCollection.size(); i++) {
                        if (!statusDependencyCollection.get(i).equals("In Progress")) {
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
                .getAsJsonObject("fields")
                .getAsJsonArray("issuelinks");

        if(teamBackLogTicketIdRLB.contains(teamBackLogId)){
            message = "Proviene del tabero RLB, por lo que no tiene dependencia asociada y, en consecuencia, esta regla no es aplicable.";
        } else {
            if (issueLinks == null || issueLinks.isEmpty()) {
                isValid = false;
                message = "Ticket no cuenta con Dependencia Asociada.";
                return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
            } else {
                List<String> isChildPadNameCollection = new ArrayList<>();
                for (JsonElement issueLinkElement : issueLinks) {
                    String type = issueLinkElement.getAsJsonObject().getAsJsonObject("type").get("inward").getAsString();
                    JsonElement inwardIssue = issueLinkElement
                            .getAsJsonObject()
                            .getAsJsonObject("inwardIssue");
                    if (type.equals("is child item of") && inwardIssue != null) {
                        String issuetype = inwardIssue.getAsJsonObject().getAsJsonObject("fields").getAsJsonObject("issuetype").get("name").getAsString();
                        if (issuetype.equalsIgnoreCase("Dependency")) {
                            isChildPadName = issueLinkElement
                                    .getAsJsonObject()
                                    .getAsJsonObject("inwardIssue")
                                    .get("key").getAsString();
                            isChildPadNameCollection.add(isChildPadName);
                        }
                    }
                }

                try {
                    for (String isChildPad : isChildPadNameCollection) {
                        var query = "key%20in%20(" + isChildPad + ")";
                        var url = ApiJiraName.URL_API_JIRA_SQL + query + new JiraApiService().getQuerySuffixURL();
                        var response = new JiraApiService().GetJiraAsync(dto.getUserName(), dto.getToken(), url);
                        JsonObject metaData = JsonParser.parseString(response).getAsJsonObject();
                        String isChildFeatureLink = metaData
                                .getAsJsonArray("issues")
                                .get(0).getAsJsonObject()
                                .getAsJsonObject("fields")
                                .get("customfield_10004").getAsString();
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
                .getAsJsonObject("fields")
                .get("summary").getAsString();
        var query = "key%20in%20(" + String.join(",", featureLink) + ")";
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
                    .getAsJsonArray("issues")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("fields")
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
            if (!summaryTicket.contains(tableroNombre)) {
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
                .getAsJsonObject("fields").get("attachment").getAsJsonArray();
        List<String> atachmentFilenameList = new ArrayList<>();
        List<String> matchedUuaas = new ArrayList<>();
        if (tipoDesarrollo.equalsIgnoreCase("mallas")) {
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
                            .getAsJsonObject("fields")
                            .getAsJsonArray("subtasks");
                    for (JsonElement subTask: subTasks) {
                        String subTaskLabel = subTask.getAsJsonObject().get("fields").getAsJsonObject().get("summary").getAsString();
                        if (subTaskLabel.equals(alphaVoBo)) {
                            String statusSubtask = subTask.getAsJsonObject().getAsJsonObject("fields").getAsJsonObject("status").get("name").getAsString();
                            if (statusSubtask.equals("Accepted")) {
                                message = "Se encontro UUAAs "+String.join(", ", matchedUuaas)+" bajo dominio de Alpha y Subtarea en estado Accepted";
                                isValid = true;
                                break;
                            }else{
                                message = "Se encontro UUAAs "+String.join(", ", matchedUuaas)+" bajo dominio de Alpha y Subtarea en estado incorrecto "+statusSubtask;
                                break;
                            }
                        }
                        else{
                            message = "Se encontro UUAAs "+String.join(", ", matchedUuaas)+" bajo dominio de Alpha sin Subtarea";
                        }
                    }

                }
            }
            else {
                message = "No se pudo Validar Alpha por no tener adjuntos.";
            }

        } else {
            message = "Esta regla no es válida para este tipo de desarrollo.";
            isValid = true;
        }
        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationItemType(String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;
        JsonObject fields = jiraTicketResult
                .getAsJsonObject("fields");
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

        JsonObject fields = jiraTicketResult.getAsJsonObject("fields");
        if (fields.has("customfield_18001")){
            String itemType = fields.getAsJsonObject("customfield_18001").get("value").getAsString();
            if(itemType.equals("Data - Dataproc")){
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
                .getAsJsonObject("fields")
                .getAsJsonArray("issuelinks");
        if (issueLinks == null || issueLinks.isEmpty()){
            isValid = false;
            message = "Ticket no cuenta con Dependencia Asociada.";
            return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
        }
        else {
            if(teamBackLogTicketIdRLB.contains(teamBackLogId)){
                message = "Esta regla no es válida para RLB.";
                return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
            }
            List<String> isChildPadNameCollection = new ArrayList<>();
            for (JsonElement issueLinkElement : issueLinks) {
                String type  = issueLinkElement.getAsJsonObject().getAsJsonObject("type").get("inward").getAsString();
                JsonElement inwardIssue = issueLinkElement
                        .getAsJsonObject()
                        .getAsJsonObject("inwardIssue");
                if(type.equals("is child item of") && inwardIssue != null) {
                    String issuetype = inwardIssue.getAsJsonObject().getAsJsonObject("fields").getAsJsonObject("issuetype").get("name").getAsString();
                    if(issuetype.equalsIgnoreCase("Dependency")) {
                        isChildPadName = issueLinkElement
                                .getAsJsonObject()
                                .getAsJsonObject("inwardIssue")
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
                    var query = "key%20in%20(" + isChildPad + ")";
                    var url = ApiJiraName.URL_API_JIRA_SQL + query + new JiraApiService().getQuerySuffixURL();
                    var response = new JiraApiService().GetJiraAsync(dto.getUserName(), dto.getToken(), url);
                    JsonObject metaData = JsonParser.parseString(response).getAsJsonObject();

                    JsonArray comments = metaData
                            .getAsJsonArray("issues")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("fields")
                            .getAsJsonObject("comment")
                            .getAsJsonArray("comments");
                    if (comments.isEmpty()){
                        isValid = false;
                        message = "Dependencia Asociada no tiene comentarios";
                        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
                    }
                    for (JsonElement comment : comments){
                        String authorEmailAddress = comment.getAsJsonObject()
                                .getAsJsonObject("author").get("emailAddress").getAsString();
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
