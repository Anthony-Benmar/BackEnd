package com.bbva.util.ApiJiraMet;

import com.bbva.common.jiraValidador.JiraValidatorConstantes;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.entities.jiravalidator.InfoJiraProject;
import com.bbva.service.JiraApiService;
import com.bbva.util.ApiJiraName;
import com.google.api.client.json.Json;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private String currentQ = "2024-Q4";
    private Map<String, Object> branchPRObject = new HashMap<>();
    private String teamBackLogId;

    public JiraValidationMethods(String jiraCode, JsonObject jiraTicketResult) throws ParseException {
        this.jiraCode = jiraCode;
        this.jiraTicketResult = jiraTicketResult;
        this.isInTableroDQA = false;
        this.isEnviadoFormulario = false;
        JsonElement featureLinkElement = this.jiraTicketResult.get("fields").getAsJsonObject().get("customfield_10004");
        this.featureLink = featureLinkElement.isJsonNull() ? null : featureLinkElement.getAsString();
        JsonElement impactLabelElement = this.jiraTicketResult.get("fields").getAsJsonObject().get("customfield_10267");
        this.impactLabel = convertJsonElementToList(impactLabelElement);
        this.branchPRObject.put("branch", "");
        this.branchPRObject.put("status", "");
        this.teamBackLogId = getTeamBackLogId();
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
                    if (items.get(0).getAsJsonObject().get("to").getAsString().equals("2461905")) { //tablero de QA
                        teamBackLogId =  items.get(0).getAsJsonObject().get("from").getAsString();
                        oldestDate = createdDate;
                    }
                }
            }
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
            //List<String> tipoDesarrolloItem = entry.getValue();

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

    public Map<String, Object> getValidatorIssueType(String helpMessage, String group) {
        Map<String, String> storyMap = new HashMap<>();
        storyMap.put("label", "Team Backlog");
        storyMap.put("field", "teamId");
        Map<String, String> dependencyMap = new HashMap<>();
        dependencyMap.put("label", "Receptor Team");
        dependencyMap.put("field", "receptorTeamId");

        Map<String, Map<String, String>> teamFieldLabelByIssueType = new HashMap<>();
        teamFieldLabelByIssueType.put("Historia", storyMap);
        teamFieldLabelByIssueType.put("Story", storyMap);
        teamFieldLabelByIssueType.put("Dependency", dependencyMap);

        var issueType = jiraTicketResult.getAsJsonObject("fields").getAsJsonObject("issuetype").get("name").getAsString();

        String message;
        boolean isValid;
        boolean isWarning = false;

        if (teamFieldLabelByIssueType.containsKey(issueType)) {
            message = "Issue Type: " + issueType;
            isValid = true;
        } else {
            message = "Issue Type inválido: " + issueType;
            String[] issueTypes = {"Story", "Dependency ( Mallas / HOST )"};
            message = message + " Atención: Solo se aceptan los siguientes Issue Types: " + String.join(", ", issueTypes);
            isValid = false;
        }
        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidatorDocumentAttachByDevType(String tipoDesarrollo) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        //FALTA VALIDAR SI ES TICKET DE INTEGRACIÓN
        var result = JiraValidatorConstantes.ATTACHS_BY_DEVELOP_TYPES.get(tipoDesarrollo);

        var attachments = jiraTicketResult.getAsJsonObject("fields").getAsJsonObject().get("attachment").getAsJsonArray();
        attachments.forEach(attachment -> {

        });

        /*
        * adjuntosWithParts = []
        for adjunto in self.adjuntos:
            adjuntoParts = adjunto.split(".")
            adjuntoSPartsSinEXT = adjuntoParts[0].split("-")
            adjuntosWithParts.append([part.strip() for part in adjuntoSPartsSinEXT][0].lower())

        isValid = adjuntoObject['label'].lower() in adjuntosWithParts
        extraLabel = f"({adjuntoObject['extraLabel']})" if "extraLabel" in adjuntoObject else ""
        message = f"""El documento <div class='{self.boxClassesBorder}'>{adjuntoObject['label']}</div> {''if  isValid else 'no'} existe {extraLabel}"""

        * */

        return getValidatonResultsDict(message, isValid, isWarning, "helpMessage", "group");
    }


    public Map<String, Object> getValidatorValidateHUTType(String helpMessage, String tipoDesarrollo, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        JsonArray issuelinks = jiraTicketResult
                .getAsJsonObject("fields")
                .getAsJsonArray("issuelinks");

        String name = null;
        String statusCategory = null;

        for (JsonElement issueLinkElement : issuelinks) {

            JsonObject issueLink = issueLinkElement.getAsJsonObject();

            if (issueLink.has("inwardIssue")) {
                JsonObject inwardIssue = issueLink.getAsJsonObject("inwardIssue");

                JsonObject issuetype = inwardIssue.getAsJsonObject("fields").getAsJsonObject("issuetype");
                JsonObject status = inwardIssue.getAsJsonObject("fields").getAsJsonObject("status");

                statusCategory = status.get("name").getAsString(); //deployes
                name = issuetype.get("name").getAsString();//story

            }
        }

        if (tipoDesarrollo.equalsIgnoreCase("procesamiento") || tipoDesarrollo.equalsIgnoreCase("kirby")) {
            if (name.equals("Story")) {
                if (statusCategory.equals("Deployed")) {
                    message = "Ticket de integracion con tickets deployados";
                    isValid = true;
                } else {
                    message = "Ticket de integracion sin tickets deployados";
                    isValid = false;
                }
            }else {
                message = "El ticket asociado debe ser de tipo Story";
                isValid = false;
            }
        } else {
            isValid = true;
            isWarning = true;
            message = "Esta regla no es válida para este tipo de desarrollo.";
        }
        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    /**
     *
     *REGLAS JUAN
     *
     */



//    private String multiReplace(String text, Map<String, String> replacements) {
//        text = (text == null) ? "" : text;
//        for (Map.Entry<String, String> entry : replacements.entrySet()) {
//            String oldString = entry.getKey();
//            String newString = entry.getValue();
//            text = text.replace(oldString, newString);
//        }
//        return text;
//    }

    ////////////////////////////getValidationValidateSubTaskValidateContractor///////////////////////////


///////////////////////_--------------------------//////////////////////////////
    ////////////////////--getValidationValidateSubTaskStatus--/////////////////////
    public String multiReplace(String text, Map<String, String> replacements) {
        if (text == null) {
            text = "";
        }
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }
        return text;
    }

//    public List<String> getSubtareasPorTipoDesarrollo(String tipoDesarrollo,boolean isIntegrationTicket
//            ,boolean isIntegrationTicketWithSubtaskPO) {
//        Map<String, List<String>> subtareasPorTipoDesarrollo = SUBTASKS_BY_DEVELOP_TYPES;
//        Map<String, List<String>> subtareasEspeciales = SUBTASKS_SPECIALS;
//        Map<String, List<String>> specialLabelsStatus = SUBTASKS_SPECIALS;
//
//        List<String> res = subtareasPorTipoDesarrollo.get(tipoDesarrollo);
//
//        // Replace development tasks with dummy change tasks
//        if (specialLabelsStatus.get("Cambio Dummy") != null) {
//            res = subtareasEspeciales.get("Cambio Dummy");
//        }
//        // Add critical path tasks if applicable
//        if (specialLabelsStatus.get("Ruta Critica") != null) {
//            res.addAll(subtareasEspeciales.get("Ruta Critica"));
//        }
//        // Replace with integration ticket tasks if it's an integration ticket without a PO subtask
//        if (isIntegrationTicket && !isIntegrationTicketWithSubtaskPO) {
//            res = subtareasEspeciales.get("Ticket Integracion");
//        }
//        return res;
//    }
//
//    public Map<String, Object> getValidationValidateSubTaskStatusList(JsonArray subTasks){
//
//
//        //SUBTASKS ***********************************************
//        subTasks.forEach(subtask -> {
//            String statusTask = subtask.getAsJsonObject()
//                    .getAsJsonObject("fields")
//                    .getAsJsonObject("status")
//                    .get("name").getAsString();
//
//        });
//
//
//    }



    //    public Map<String, Object> getValidationValidateSubTaskStatus(Map<String, Object> currentSubTaskDetected, String helpMessage, String group) {
//        String message = "";
//        boolean isValid = false;
//        boolean isWarning = false;
//
//        //SUBTAREAS
//        JsonArray subtasks = jiraTicketResult
//                .getAsJsonObject("fields")
//                .getAsJsonArray("subtasks");
//
//        //Obtener subtareas por tipo de desarrollo o tipo de Ticket (cambio dummy, incidencias)
//        List<String> subtasks_1 = getSubtareasPorTipoDesarrollo( "mallas", false, false);
//        System.out.println(subtasks_1);
//
//        //Se valida cada una de las subtareas que deben existir
//        //SUBTASKS ***********************************************
//        subtasks.forEach(subtask -> {
//            String statusTask = subtask.getAsJsonObject()
//                    .getAsJsonObject("fields")
//                    .getAsJsonObject("status")
//                    .get("name").getAsString();
//        });
//
//        if (currentSubTaskDetected.get("status").equals(currentSubTaskDetected.get("owner").get("object").get("status")) || jiraTicketStatus.equals("Deployed") || currentSubTaskDetected.get("owner").get("object").get("advertenciaEstadoInicial")) {
//            message = "With status: " + currentSubTaskDetected.get("status");
//            isValid = true;
//
//            if (currentSubTaskDetected.get("status").equals("Ready To Verify") && currentSubTaskDetected.get("owner").get("object").get("advertenciaReadyToVerify") == true) {
//                message += "Attention: The Subtask must change to Accepted before deploying to production, otherwise the " + ticketVisibleLabel + " will be blocked";
//                isWarning = true;
//            }
//
//            List<String> listaEstadosTicketSinAdvertencia = Arrays.asList("Deployed");
//
////            if (!currentSubTaskDetected.get("status").equals(currentSubTaskDetected.get("owner").get("object").get("status")) && currentSubTaskDetected.get("owner").get("object").get("advertenciaEstadoInicial") == true) {
////                if (isInTableroDQA && isEnviadoFormulario) {
////                    isValid = true;
////                    isWarning = !jiraTicketStatus.equals(listaEstadosTicketSinAdvertencia);
////
////                    if (isWarning) {
////                        message += "Attention: It's possible that the " + ticketVisibleLabel + " is under review. Remember that the initial state of a " + ticketVisibleLabel + " to be reviewed is Ready";
////                    }
////                } else {
////                    isValid = false;
////                }
////            }
//        } else {
//            message = "With invalid status: " + currentSubTaskDetected.get("status");
//            isValid = false;
//        }
//
////        if (subtaskObject.containsKey("messagePrefix")) {
////            message = subtaskObject.get("messagePrefix") + " " + message;
////        }
//
//        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
//    }
/////////////////////////////////////getValidationValidateSubTaskValidateContractor///////////////////////////////////

    //////////////////////--FIN--/////////////////////


//    public Map<String, Object> getValidationValidateSubTaskValidateContractor(String tipoDesarrollo) {
//        String message = "";
//        AtomicBoolean isValid = new AtomicBoolean(true);
//        boolean isWarning = false;
//        var result = SUBTASKS_SPECIALS.get(tipoDesarrollo);
//
//        JsonArray subTasks = jiraTicketResult
//                .getAsJsonObject("fields")
//                .getAsJsonArray("subtasks");
//
//        //Obtener las subtareas por tipo de desarrollo o tipo de Ticket (cambio dummy, incidencias)
//        List<JsonObject> subTaskCollection = new ArrayList<>();
//        subTasks.forEach(subtask -> {
//            String statusSubTask =subtask.getAsJsonObject()
//                    .getAsJsonObject("fields")
//                    .get("summary").getAsString();
//            if (statusSubTask.contains("PO") || statusSubTask.contains("LT")){
//                subTaskCollection.add(subtask.getAsJsonObject());
//                System.out.println(subTaskCollection);
//            }
//        });
//
//
//
////        if (!(.contains(".contractor")) {
////            message = subTaskAcceptedRow.get("email") + " es Interno BBVA";
////            isValid = true;
////        } else {
////            message = "Subtarea " + subTaskLabel + "\n" + subTaskAcceptedRow.get("email") + " no es Interno BBVA";
////            isValid = false;
////        }
////
////        if (subtaskObject.containsKey("messagePrefix")) {
////            message = subtaskObject.get("messagePrefix") + " " + message;
////        }
//
//        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
//    }



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
        //String statusIcon = statusIcons[result.get("isValid").equals(true) ? "valid" : "invalid"];
        Map<String, Object> newMessage = new HashMap<>();
        newMessage.put("message", result.get("message"));
        newMessage.put("helpMessage", result.get("helpMessage"));
        newMessage.put("isValid", result.get("isValid"));
        newMessage.put("isWarning", result.get("isWarning"));
        //newMessage.put("statusIcon", statusIcon);
        newMessage.put("group", result.get("group"));
        return newMessage;
    }
//-----------------------------------------------------------------------------------------------
    //REGLA OK
public Map<String, Object> getValidationPR(String tipoDesarrollo, String helpMessage, String group) {
    String message = "";
    boolean isValid = false;
    boolean isWarning = false;
    List<String> prsUrls = new ArrayList<>();
    int cantidadPRs = 0;
    JsonObject jiraTicketResultPrs = jiraTicketResult
            .getAsJsonObject("fields");

    // se obtienen las PRs asociadas al ticket
    List<String> tipoDesarrolloPRs = Arrays.asList("Procesamiento","MigrationTool",
            "Hammurabi", "Ingesta", "Scaffolder", "Operativizacion",
            "Teradata", "SmartCleaner","SparkCompactor", "JSON Global");
    //convertir todos los elementos del tipoDesarrolloPRs a minúsculas
    tipoDesarrolloPRs.replaceAll(String::toLowerCase);

    if (jiraTicketResultPrs.get("prs") != null) {
        cantidadPRs = jiraTicketResultPrs.get("prs").getAsJsonArray().size();
        // por defecto solo deberia venir una pr, asi que se tomara la primera
        if (cantidadPRs > 0) {
            this.branchPRObject.put("branch",jiraTicketResultPrs.get("prs").getAsJsonArray().get(0).getAsJsonObject().get("destinyBranch").getAsString());
            this.branchPRObject.put("status",jiraTicketResultPrs.get("prs").getAsJsonArray().get(0).getAsJsonObject().get("status").getAsString());
        }
        // se obtienen las urls de las PRs
        for (JsonElement prObj : jiraTicketResultPrs.get("prs").getAsJsonArray()) {
            Map<String, Object> pr = new HashMap<>();
            pr.put("url", prObj.getAsJsonObject().get("url").getAsString());
            prsUrls.add(pr.get("url").toString());
        }
    }

    if (tipoDesarrollo.equals("PRs") || tipoDesarrollo.equals("mallas") || tipoDesarrolloPRs.contains(tipoDesarrollo)) {
        // validar que se tenga solo 1 PR
        if (cantidadPRs == 1) {
            message = "Con PR asociada: " + prsUrls.get(0);
            isValid = true;
            // setear self.urlPRActual y self.prActual aquí si es necesario
        } else if (cantidadPRs > 1) {
            message = "Se encontraron " + cantidadPRs + " PRs asociadas: " + String.join(", ", prsUrls);
            isValid = false;
            message += "Atención: No se puede tener más de una PR asociada.";
            isWarning = true;
        } else {
            message = "No se detectó una PR asociada.";
            isValid = false;
            message += "Atención: Si la PR fue asociada correctamente, falta dar permisos de acceso a los QEs.";
        }
    } else {
        // validar que no se tenga PRs
        // por las dudas limpiar url de pr actual, asi en el futuro si se asigna por error, no mandara ningun resultado
        // self.urlPRActual = "";
        if (cantidadPRs == 0) {
            message = "Sin PR asociada.";
            isValid = true;
        } else {
            message = "No se debe asociar una PR a este Tipo de Desarrollo.";
            isValid = false;
        }
    }
    return this.getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
}
    public Map<String, Object> getValidationPRBranch(String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        List<String> validBranches = Arrays.asList("develop", "master");

        message = "Rama destino: " + this.branchPRObject.get("branch");
        isValid = validBranches.contains(this.branchPRObject.get("branch"));

        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationValidateSubTaskStatus(String tipoDesarrollo,String helpMessage, String group) {
        AtomicReference<String> message = new AtomicReference<>("");
        AtomicBoolean isValid = new AtomicBoolean(true);
        boolean isWarning = false;
        var results = SUBTASKS_BY_DEVELOP_TYPES.get(tipoDesarrollo) == null ? new ArrayList<>() : SUBTASKS_BY_DEVELOP_TYPES.get(tipoDesarrollo);
        List<JsonObject> subTaskCollection = new ArrayList<>();

        if (results.isEmpty()){
            isValid.set(false);
            message.set("Sin tipo de desarrollo asignado");
        }else {
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
                isValid.set(false);
            }else {
                try {
                    subTaskCollection.forEach(subTask -> {
                        message.set("Todas las subtareas tienen el estado Aceptado");
                        if (!subTask.getAsJsonObject()
                                .getAsJsonObject("fields")
                                .getAsJsonObject("status")
                                .get("name").getAsString().equals("Accepted")){
                            isValid.set(false);
                            message.set("Subtarea sin estado Accepted: " + subTask.getAsJsonObject("fields").get("summary").getAsString());
                            throw new RuntimeException("Break");
                        }
                    });
                } catch (RuntimeException e) {
                    if (!e.getMessage().equals("Break")) throw e;
                }
            }
        }

        return getValidationResultsDict(message.get(), isValid.get(), isWarning, helpMessage, group);



//        String message = "";
//        boolean isValid = false;
//        boolean isWarning = false;
//        Map<String, List<String>> subtareasPorTipoDesarrollo = SUBTASKS_SPECIALS;
//        List<String> statusTableroDQA = Arrays.asList("Ready", "In Progress", "Test", "Ready To Verify", "Ready To Deploy", "Deployed", "Accepted");
//
//        //ESTADO DEL TICKET
//        String jiraTicketStatus = jiraTicketResult
//                .getAsJsonObject("fields")
//                .getAsJsonObject("status").get("name").getAsString();
//        //SUBTAREAS
//        JsonArray subtasks = jiraTicketResult
//                .getAsJsonObject("fields")
//                .getAsJsonArray("subtasks");
//
//        if (jiraTicketStatus.equals("Deployed")) {
//            message = "Con estado " + jiraTicketStatus;
//            isValid = true;
//        } else {
//            message = "Con estado inválido: ";
//            isValid = false;
//        }
//        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationValidateSubtaskPerson(JiraValidatorByUrlRequest dto,String tipoDesarrollo, String helpMessage, String group, List<InfoJiraProject> infoJiraProjectList) {
        AtomicReference<String> message = new AtomicReference<>("Todas Las subtareas tienen el VoBo de la persona asociada al proyecto");
        AtomicBoolean isValid = new AtomicBoolean(true);
        boolean isWarning = false;
        try {
            JsonArray subTasks = jiraTicketResult
                    .getAsJsonObject("fields")
                    .getAsJsonArray("subtasks");
            if(teamBackLogId == null || teamBackLogId.isEmpty()){
                message.set("HU sin Team BackLog");
                isValid.set(false);
            } else {
                subTasks.forEach(subtask -> {
                    String summarySubTask = subtask.getAsJsonObject()
                            .getAsJsonObject("fields")
                            .get("summary").getAsString();
                    String codeJiraSubTask = subtask.getAsJsonObject().get("key").getAsString();
                    var tickets = List.of(codeJiraSubTask);
                    var query = "key%20in%20(" + String.join(",", tickets) + ")";
                    JsonObject metaData = null;

                    var url = ApiJiraName.URL_API_JIRA_SQL + query + new JiraApiService().getQuerySuffixURL();
                    String response = null;
                    try {
                        response = new JiraApiService().GetJiraAsync(dto.getUserName(), dto.getToken(), url);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    metaData = JsonParser.parseString(response).getAsJsonObject();

                    JsonObject assignee = metaData
                            .getAsJsonArray("issues")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("fields")
                            .getAsJsonObject("assignee");
                    String asigneeSubtask = assignee.get("emailAddress").getAsString();
                    if (asigneeSubtask == null || asigneeSubtask.isEmpty()) {
                        message.set("Subtareas sin asignación");
                        isValid.set(false);
                    } else {
                        for (Map.Entry<String, Map<String, Object>> entry : SUBTASKS_TYPE_OWNER.entrySet()) {
                            List<String> items = (List<String>) entry.getValue().get("items");
                            if (items != null && items.contains(summarySubTask) && (boolean)entry.getValue().get("validateEmailFromLideres")) {
                                List<InfoJiraProject> projectsFiltrados = infoJiraProjectList.stream().filter(
                                        project -> ((List<String>) entry.getValue().get("rol")).contains(project.getProjectRolType())
                                                && project.getTeamBackLogId().equals(teamBackLogId)
                                                && project.getParticipantEmail().equals(assignee.get("emailAddress").getAsString())
                                ).collect(Collectors.toList());
                                if (projectsFiltrados.isEmpty()) {
                                    if(!isValid.get()){
                                        message.set(message.get()+"\n"+"La persona "+ asigneeSubtask + ", no se encuentra asociada en el proyecto para la subtarea "+ summarySubTask+".");
                                    }
                                    else {
                                        message.set("La persona " + asigneeSubtask + ", no se encuentra asociada en el proyecto para la subtarea " + summarySubTask+".");
                                        isValid.set(false);
                                    }
                                }
                                break;
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return getValidationResultsDict(message.get(), isValid.get(), isWarning, helpMessage, group);
    }


    //Ruler OK
    public Map<String, Object> getValidationValidateSubTaskValidateContractor(JiraValidatorByUrlRequest dto, String helpMessage, String group) {
        AtomicReference<String> message = new AtomicReference<>("");
        AtomicBoolean isValid = new AtomicBoolean(false);
        AtomicBoolean noAsignee = new AtomicBoolean(false);
        boolean isWarning = false;

        Map<String, Object> subTaskAcceptedRow = new HashMap<>(); //Es un elemento de la lista de subtareas que han sido validadas con "Accepted"
        JsonArray subTasks = jiraTicketResult
                .getAsJsonObject("fields")
                .getAsJsonArray("subtasks");

        List<JsonObject> subTaskAsigCollection = new ArrayList<>();

        subTasks.forEach(subtask -> {
            String codeJiraSubTask = subtask.getAsJsonObject().get("key").getAsString();
            var tickets = List.of(codeJiraSubTask);
            var query = "key%20in%20(" + String.join(",", tickets) + ")";
            JsonObject metaData = null;
            try {
                var url = ApiJiraName.URL_API_JIRA_SQL + query + new JiraApiService().getQuerySuffixURL();
                var response = new JiraApiService().GetJiraAsync(dto.getUserName(),dto.getToken(),url);
                metaData = JsonParser.parseString(response).getAsJsonObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            List<String> huSubtaskValid = Arrays.asList("[PO]", "[KM]", "[AT]", "[SO]");
            JsonElement issue = metaData
                    .getAsJsonArray("issues")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("fields");
            String summary = issue.getAsJsonObject().get("summary").getAsString();

            boolean matchFound = huSubtaskValid.stream().anyMatch(summary::contains);

            if (matchFound) {
                JsonObject assignee = metaData
                        .getAsJsonArray("issues")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("fields")
                        .getAsJsonObject("assignee");
                if(assignee != null){
                    subTaskAsigCollection.add(assignee);
                }
                else {
                    message.set("Subtarea " + summary + " no tiene correo asignado");
                    isValid.set(false);
                    noAsignee.set(true);
                }
            }
        });
        if (noAsignee.get()) {
            return getValidatonResultsDict(message.get(), isValid.get(), isWarning, helpMessage, group);
        }
        List<String> invalidEmails = new ArrayList<>();
        for (JsonObject asig : subTaskAsigCollection) {
            if (asig.get("emailAddress").getAsString().contains(".contractor")) {
                invalidEmails.add(asig.get("emailAddress").getAsString());
            }
        }

        if (!invalidEmails.isEmpty()) {
            message.set("Subtareas " + String.join(", ", invalidEmails) + " no son Interno BBVA");
            isValid.set(false);
        } else {
            message.set("Todos los correos son Interno BBVA");
            isValid.set(true);
        }
        return getValidatonResultsDict(message.get(), isValid.get(), isWarning, helpMessage, group);
    }

    //FALTA
    public Map<String, Object> getValidationAcceptanceCriteria(String tipoDesarrollo, String helpMessage, String group) {
        String message = "";
        boolean isValid = false;

        String acceptanceCriteriaString = jiraTicketResult
                .getAsJsonObject("fields")
                .get("customfield_10260").getAsString();

        acceptanceCriteriaString = acceptanceCriteriaString.replaceAll("\\s+", " ").trim();
        acceptanceCriteriaString = acceptanceCriteriaString.replaceAll("\\{\\}", ""); // Elimina llaves vacías
        String acceptanceCriteria = acceptanceCriteriaString.replace("*", "").trim();  // Elimina los asteriscos
        acceptanceCriteria = acceptanceCriteria.replaceAll("\\p{Z}", " ").trim();

        Map<String, Object> validAcceptanceCriteriaObject = CRITERIA_BY_DEVELOP_TYPES.get(tipoDesarrollo);

        if (!acceptanceCriteria.isEmpty()) {
            if (validAcceptanceCriteriaObject != null) {
                String expectedPattern = (String) validAcceptanceCriteriaObject.get("texto");

                expectedPattern = expectedPattern
                        .replace("{0}", "[A-Za-z\\s-]+")  // Captura el nombre del plan (por ejemplo, Plan Cross Sell FX)
                        .replace("{1}", "SDATOOL-\\d+\\s+con\\s+MVP\\s+D-\\d+-\\d+,");  // Captura el SDATOOL, MVP y la coma después

                String regexPattern = expectedPattern
                        .replaceAll("\\s+", "\\\\s+")
                        .replaceAll("\\.", "\\\\.");

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

        return getValidatonResultsDict(message, isValid, false, helpMessage, group);
    }


    //REGLA QUE NECESITA LA VALIDACION DEL ENVIO DE FORMULARIO
    public Map<String, Object> getValidationTeamAssigned(String tipoDesarrollo, boolean validacionEnvioFormulario, String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        String jiraTicketStatus = jiraTicketResult
                .getAsJsonObject("fields")
                .getAsJsonObject("status")
                .get("name").getAsString();
        String teamIdDQA = "2461905";
        //boolean isInTableroDQA = false; //debería ser variable global
        //String tipoDesarrollo = "";
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
        //convertir todos los elementos del statusTableroDQA a minúsculas
        statusTableroDQA.replaceAll(String::toLowerCase);

        JsonArray histories = this.jiraTicketResult.getAsJsonObject("changelog").getAsJsonArray("histories");

        for (JsonElement historyElement : histories) {
            JsonObject history = historyElement.getAsJsonObject();

            if (history.has("items")) {
                JsonArray items = history.getAsJsonArray("items");
                for (JsonElement itemElement : items) {
                    JsonObject item = itemElement.getAsJsonObject();

                    if (item.has("field") && item.get("field").getAsString().equals(currentTeamFieldLabel)) {
                        // Obtener otros valores, por ejemplo "fromString" y "toString"
                        String from = item.get("from").getAsString();
                        String to = item.get("to").getAsString();

                        HashMap<String, String> currentTeam = new HashMap<>();
                        currentTeam.put("from", from);
                        currentTeam.put("id", to);

                        if (currentTeam != null && (currentTeam.get("id").equals(teamIdDQA))) {
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

    // REGLA OK
    public Map<String, Object> getValidationValidateJIRAStatus(String tipoDesarrollo, String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        //String tipoDesarrollo = "";

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
//OK
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
//OK
    public Map<String, Object> getValidationFeatureLinkPAD3(String helpMessage, String group) {
        Map<String, Object> result = new HashMap<>();
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;


        if (featureLink == null || featureLink.isBlank()) {
            message = "Sin Feature Link asociado";
            isValid = false;
        } else {


            // Verificamos si featureLink comienza con "PAD3-"
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
//OK - modificar debe dar alerta no fallar
    public Map<String, Object> getValidationFeatureLinkStatus(JiraValidatorByUrlRequest dto, String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;
        JsonObject metaData = null;
        List<String> validStatuses = Arrays.asList("In Progress", "Test", "Ready To Verify", "Ready To Deploy", "Deployed");
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

                var jiraTicketStatus = jiraTicketResult.get("fields").getAsJsonObject().get("status").getAsJsonObject().get("name").getAsString();

                if (!jiraTicketStatus.equals("Deployed")){
                    if (Arrays.asList("Ready To Verify", "Ready To Deploy", "Deployed").contains(featureLinkStatus)) {
                        message += "Atención: Revisar estado del Feature Link, debe estar en In Progress cuando se encuentre en revisión de DQA, " + coordinationMessage;
                        isWarning = true;
                    }
                }
            } else {
                message = "Con estado " + featureLinkStatus;
            }
        }
        return this.getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
    }
//OK - el mensaje debe tomar solo la cadena no el arreglo
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
            //metaData.getAsJsonObject("fields").getAsJsonObject("customfield_10264");

            String jiraTicketStatus = jiraTicketResult.get("fields").getAsJsonObject().get("status").getAsJsonObject().get("name").getAsString();
//        if (programIncrement != null) {
//            featureProgramIncrement = programIncrement;
//        }

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
//Pendiente
//    public Map<String, Object> getValidationValidateSubTask(String tipoDesarrollo, String helpMessage, String group) {
//        String message = "";
//        boolean isValid = false;
//        boolean isWarning = false;
//        Map<String, Object> subTask = null;
//
//        boolean wasFound = false;
//        for (Map.Entry<String, Map<String, Object>> entry : this.subTasksBySummary.entrySet()) {
//            String subTaskLabel = entry.getKey();
//            Map<String, Object> subTaskItem = entry.getValue();
//            if (subTaskLabel.equals(subtaskObject.get("label"))) {
//                subTask = new HashMap<>();
//                subTask.put("key", subTaskItem.get("key"));
//                subTask.put("label", subTaskLabel);
//                subTask.put("item", subTaskItem);
//                subTask.put("subtaskObject", subtaskObject);
//                subTask.put("status", subTaskItem.get("status"));
//
//                for (Map.Entry<String, Map<String, Object>> ownerEntry : this.subtareasTipoOwner.entrySet()) {
//                    String keySubtaskOwner = ownerEntry.getKey();
//                    Map<String, Object> itemSubtaskOwner = ownerEntry.getValue();
//                    if (((List<String>) itemSubtaskOwner.get("items")).contains(subTaskLabel)) {
//                        Map<String, Object> owner = new HashMap<>();
//                        owner.put("key", keySubtaskOwner);
//                        owner.put("object", itemSubtaskOwner);
//                        subTask.put("owner", owner);
//                    }
//                }
//
//                String subtareaURL = this.jiraBasePrefix + "/" + subTask.get("key");
//                message = "Subtarea " + subtareaURL + " encontrada ";
//                isValid = true;
//                wasFound = true;
//                break;
//            }
//        }
//
//        if (!wasFound) {
//            String similarSubtask = "";
//            List<String> subTaskLabelParts = Arrays.asList(subtaskObject.get("label").toString().replace("[", "").split("]"));
//            for (Map.Entry<String, Map<String, Object>> entry : this.subTasksBySummary.entrySet()) {
//                String subtaskKey = entry.getKey();
//                int foundItems = 0;
//                for (String part : subTaskLabelParts) {
//                    if (subtaskKey.contains(part)) {
//                        foundItems++;
//                    }
//                }
//                if (foundItems == subTaskLabelParts.size()) {
//                    similarSubtask = subtaskKey;
//                }
//            }
//
//            message = "No encontrada";
//            if (!similarSubtask.isEmpty()) {
//                message += ", se encontró " + similarSubtask;
//            }
//            if (subtaskObject.containsKey("messagePrefix")) {
//                message = subtaskObject.get("messagePrefix") + " " + message;
//            }
//            isValid = false;
//        }
//
//        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
//    }

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
                message += " Tambien se encontraron subtareas adicionales: " + String.join(", ", additionalSubTasks);
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
        } else {
            // Encontrar las subtareas que faltan
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
//Por revisar
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
            message = "Impact Label no es necesario para este tipo de desarrollo";
            isValid = true;
        }

        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
    }
//Por revisar
    public Map<String, Object> getValidationFixVersion(String tipoDesarrollo, String helpMessage, String group) {
        String message = "";
        boolean isValid;
        boolean isWarning = false;
        if (tipoDesarrollo.equals("HOST") || tipoDesarrollo.equals("mallas")) {
            System.out.println();
            String[] jiraCodeParts = this.jiraCode.split("-");
            String jiraPADCode = jiraCodeParts[0].toUpperCase();

            JsonArray fixVersions = this.jiraTicketResult.getAsJsonObject("fields").getAsJsonArray("fixVersions");

            if (!fixVersions.isEmpty()) {
                String fixVersionURLPrefix = ApiJiraName.URL_API_BASE + "/issues?jql=project=" + jiraPADCode + "%20AND%20fixVersion=";

                List<String> fixVersionsUrlLinkList = new ArrayList<>();
                for (JsonElement fixVersion : fixVersions) {
                    String fixVersionName = fixVersion.getAsJsonObject().get("name").getAsString();
                    String fixVersionUrl = fixVersionURLPrefix + fixVersionName;
                    fixVersionsUrlLinkList.add(fixVersionUrl + fixVersionName);
                }

                message = "Con Fix Version " + fixVersionsUrlLinkList;
                isValid = true;
            } else {
                message = "Sin Fix Version asignado";
                isValid = false;
            }
        }
         else{
                message = "Fix Version no es necesario para este tipo de desarrollo";
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

       for (JsonElement issueLinkElement : issuelinks) {
           JsonObject issueLink = issueLinkElement.getAsJsonObject();

           if (issueLink.has("inwardIssue")) {
               JsonObject inwardIssue = issueLink.getAsJsonObject("inwardIssue");

               JsonObject issuetype = inwardIssue.getAsJsonObject("fields").getAsJsonObject("issuetype");
               JsonObject status = inwardIssue.getAsJsonObject("fields").getAsJsonObject("status");

               statusCategory = status.get("name").getAsString(); //deployes
               name = issuetype.get("name").getAsString();//story
           }
       }

       if (tipoDesarrollo.equalsIgnoreCase("productivizacion")) {
           if (name.equals("Story")) {
               if (statusCategory.equalsIgnoreCase("Deployed")) {
                   message = "Todos los tickets asociados se encuentran deployados";
                   isValid = true;
               } else {
                   message = "No todos los tickets asociados se encuentran deployados";
               }
           } else{
               message = "El ticket asociado no es de tipo Story";
           }
       } else {
           isWarning = true;
           message = "Esta regla no es válida para este tipo de desarrollo.";
       }

       return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
   }

   //LABELS_BY_DEVELOP_TYPES
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

    public Map<String, Object> getValidationDependency(String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        String statusDependencyTicket = "";
        //"In Progress"

        JsonArray issueLinks = jiraTicketResult
                .getAsJsonObject("fields")
                .getAsJsonArray("issuelinks");

        if (issueLinks == null || issueLinks.isEmpty()){
            isValid = false;
            message = "Ticket no cuenta con Dependencia Asociada, Solo RLB tiene Excepción";
        }
        else {
            List<String> statusDependencyCollection = new ArrayList<>();
            List<String> dependencyPadCollection = new ArrayList<>();
            for (JsonElement issueLinkElement : issueLinks) {
                statusDependencyTicket = issueLinkElement
                        .getAsJsonObject()
                        .getAsJsonObject("inwardIssue")
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
        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
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
                        fromString = "";
                    } else {
                        fromString = items.get(0).getAsJsonObject().get("fromString").getAsString();
                    }
                    Pattern pattern = Pattern.compile("<span style=\"color: #fff\">(.*?)</span>");
                    Matcher matcher = pattern.matcher(fromString);
                    if (matcher.find()) {
                        extractedContent = matcher.group(1);
                    }
                    if (extractedContent.contains("Data Quality")) {
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

    public Map<String, Object> getValidationDependencyFeatureVsHUTFeature(JiraValidatorByUrlRequest dto, String helpMessage, String group) {
        boolean isValid = true;
        String message = "Todas las dependencias tienen el mismo features link";
        boolean isWarning = false;

        String isChildPadName ="";
        JsonArray issueLinks = jiraTicketResult
                .getAsJsonObject("fields")
                .getAsJsonArray("issuelinks");
        if (issueLinks == null || issueLinks.isEmpty()){
            isValid = false;
            message = "Ticket no cuenta con Dependencia Asociada, Solo RLB tiene Excepción";
        }
        else {
            List<String> isChildPadNameCollection = new ArrayList<>();
            for (JsonElement issueLinkElement : issueLinks) {
                isChildPadName = issueLinkElement
                        .getAsJsonObject()
                        .getAsJsonObject("inwardIssue")
                        .get("key").getAsString();
                isChildPadNameCollection.add(isChildPadName);
            }

            try {
                JiraApiService jiraApiService = new JiraApiService();
                for (String isChildPad : isChildPadNameCollection) {
                    var query = "key%20in%20(" + isChildPad + ")";
                    var url = ApiJiraName.URL_API_JIRA_SQL + query + jiraApiService.getQuerySuffixURL();
                    var response = jiraApiService.GetJiraAsync(dto.getUserName(), dto.getToken(), url);
                    JsonObject metaData = JsonParser.parseString(response).getAsJsonObject();
                    String isChildFeatureLink = metaData
                            //customfield_10004
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
        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
    }



    public  HashMap<String, Object> getValidationValidateSubtaskAssociate(String tipoDesarrollo, String s, String subtask) {

        return new HashMap<>();
    }

    public Map<String, Object> getValidationBoardProject(JiraValidatorByUrlRequest dto, String helpMessage, String feature_link, String group,List<InfoJiraProject> infoJiraProjectList) {
        AtomicReference<String> message = new AtomicReference<>("El tablero es valido");
        AtomicBoolean isValid = new AtomicBoolean(true);
        boolean isWarning = false;
        String summaryTicket =  jiraTicketResult.getAsJsonObject()
                .getAsJsonObject("fields")
                .get("summary").getAsString();
        String teamBackLogTicketId =jiraTicketResult
                .getAsJsonObject()
                .getAsJsonObject("fields")
                .get("customfield_13301").getAsString(); //customfield_13300
        var query = "key%20in%20(" + String.join(",", featureLink) + ")";
        JsonObject metaData = null;
        try {
            var url = ApiJiraName.URL_API_JIRA_SQL + query + new JiraApiService().getQuerySuffixURL();
            var response = new JiraApiService().GetJiraAsync(dto.getUserName(), dto.getToken(), url);
            metaData = JsonParser.parseString(response).getAsJsonObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String teamBackLogFeatureId = metaData
                .getAsJsonArray("issues")
                .get(0).getAsJsonObject()
                .getAsJsonObject("fields")
                .getAsJsonArray("customfield_13300").get(0).getAsString();
        if (!teamBackLogFeatureId.equals(teamBackLogTicketId)){
            message.set("El tablero del Ticket es distinto al tablero del Feature");
            isValid.set(false);
        }
        List<InfoJiraProject> projectFiltrado =  infoJiraProjectList.stream().filter(project ->  project.getTeamBackLogId() != null
                && project.getTeamBackLogId().equals(teamBackLogTicketId)).collect(Collectors.toList());
        if(!projectFiltrado.isEmpty()) {
            String tableroNombre = projectFiltrado.get(0).getTeamBackLogName();
            if(!summaryTicket.contains(tableroNombre)) {
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
        List<String> alphaUuaas = List.of("KUSU", "KLIM", "KFUL", "ATAU", "KSKR",
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
        }
        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
    }
}
