package com.bbva.util.ApiJiraMet;

import com.bbva.common.jiraValidador.JiraValidatorConstantes;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.service.JiraApiService;
import com.bbva.util.ApiJiraName;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.bbva.common.jiraValidador.JiraValidatorConstantes.*;

public class ValidationUrlJira {
    private  String jiraCode ;
    private final String validPADList = "pad3,pad5";
    private final String ticketGroup = "Ticket";
    private JsonObject jiraTicketResult;
    private boolean isInTableroDQA;
    private boolean isEnviadoFormulario;

    public ValidationUrlJira(String jiraCode, JsonObject jiraTicketResult) {
        this.jiraCode = jiraCode;
        this.jiraTicketResult = jiraTicketResult;
        this.isInTableroDQA = false;
        this.isEnviadoFormulario = false;

    }

    public  Map<String, Object> getValidatorProjectPAD(String helpMessage, String group) {
        String message;
        boolean isValid;
        boolean isWarning = false;

        String[] jiraCodeParts = this.jiraCode.split("-");
        String jiraPADCode = jiraCodeParts[0].toUpperCase();

        if (this.validPADList.contains(jiraPADCode.toLowerCase())) {
            message = "Se encontró " + jiraPADCode.toLowerCase();
            isValid = true;
        } else {
            message = "No se encontró " + String.join(" o ", this.validPADList);
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
        String message ="";
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



    public Map<String, Object> getValidatorValidateHUTType(String helpMessage, String tipoDesarrolloSummary, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;
        ArrayList<String> projectCodeList = new ArrayList<>(Arrays.asList("PAD3", "PAD5"));

        if (projectCodeList.contains(this.jiraCode) && tipoDesarrolloSummary != ""){
            var tipoDesarrollo = tipoDesarrolloSummary;
            /*if "scaffolder" in self.tipoDesarrolloFormulario.lower() and "despliegue" not in tipoDesarrolloFormulario.lower(){
                var tipoDesarrollo = "Scaffolder";
                message ="Tipo de desarrollo" + tipoDesarrollo;
                isValid = true;
            }*/
        }else {
            message = "No se pudo detectar el tipo de desarrollo";
            isValid = false;
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

    public Map<String, Object> getValidationValidateSubTaskStatus(String tipoDesarrollo) {
        String message = "";
        AtomicBoolean isValid = new AtomicBoolean(true);
        boolean isWarning = false;
        var results = VOBO_BY_DEVELOP_TYPES.get("cambio dummy");

        JsonArray subTasks = jiraTicketResult
                .getAsJsonObject("fields")
                .getAsJsonArray("subtasks");

        //Obtener las subtareas por tipo de desarrollo o tipo de Ticket (cambio dummy, incidencias)
        List<JsonObject> subTaskCollection = new ArrayList<>();
        subTasks.forEach(subtask -> {
            String statusSubTask =subtask.getAsJsonObject()
                    .getAsJsonObject("fields")
                    .get("summary").getAsString();

            results.forEach(result -> {
                if (statusSubTask.contains(result)){
                    subTaskCollection.add(subtask.getAsJsonObject());
                    System.out.println(subTaskCollection);
                }
            });
        });
        if (subTaskCollection.isEmpty()){
            isValid.set(false);
        }else {
            message = "Subtareas encontradas";
            subTaskCollection.forEach(subTask -> {
                if (!subTask.getAsJsonObject()
                        .getAsJsonObject("fields")
                        .getAsJsonObject("status")
                        .get("name").getAsString().equals("Accepted")){
                    isValid.set(false);
                }
            });
        }

        return getValidationResultsDict(message, isValid.get(), isWarning, "Validar que el email del contractor sea correcto", "group");



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

    public Map<String, Object> getValidationValidateSubTaskValidateContractor(JiraValidatorByUrlRequest dto, String helpMessage, String group) {
        AtomicReference<String> message = new AtomicReference<>("");
        AtomicBoolean isValid = new AtomicBoolean(false);
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

            JsonObject assignee = metaData
                    .getAsJsonArray("issues")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("fields")
                    .getAsJsonObject("assignee");
            //.get("emailAddress").getAsString();
            subTaskAsigCollection.add(assignee);
        });

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

//        if (subtaskObject.containsKey("messagePrefix")) {
//            message = String.format("%s %s", subtaskObject.get("messagePrefix"), message);
//        }

        return getValidatonResultsDict(message.get(), isValid.get(), isWarning, helpMessage, group);
    }

        public Map<String, Object> getValidationAcceptanceCriteria(String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;
        String tipoDesarrollo = "";

        Map<String, String> acceptanceCriteriaReplacements = new HashMap<>();
        acceptanceCriteriaReplacements.put("*", "");
        acceptanceCriteriaReplacements.put("_MVP_", "MVP");
        acceptanceCriteriaReplacements.put(".", "");
        acceptanceCriteriaReplacements.put("\"", "");
        acceptanceCriteriaReplacements.put("“", "");
        acceptanceCriteriaReplacements.put("”", "");

        String acceptanceCriteriaString = jiraTicketResult
                .getAsJsonObject("fields")
                .get("customfield_10260").getAsString();

        String acceptanceCriteria = multiReplace(acceptanceCriteriaString, acceptanceCriteriaReplacements);
        Map<String, Object> validAcceptanceCriteriaObject = CRITERIA_BY_DEVELOP_TYPES.get(tipoDesarrollo);
        //String errorTemplateCriterioAceptacionText = String.format("Atención<br>Debe tener un formato similar a: %s", validAcceptanceCriteriaObject.get("texto"));

        if (!acceptanceCriteria.isEmpty()) {
            if (tipoDesarrollo.equals("Mallas") || tipoDesarrollo.equals("HOST")) {
//                if (!tipoIncidenciaKey.isEmpty()) {
//                    if (validAcceptanceCriteriaObject.get("textoTipoLabelEspecial").get("labelKeys").contains(tipoIncidenciaKey)) {
//                        validAcceptanceCriteriaObject.put("texto", validAcceptanceCriteriaObject.get("textoTipoLabelEspecial").get("texto"));
//                    }
//                } else {
//                    Matcher matcher = Pattern.compile(regexMallasMVP).matcher(acceptanceCriteria);
//                    if (matcher.find()) {
//                        mvpMallasDetectado = matcher.group(0);
//                        validAcceptanceCriteriaObject.put("texto", validAcceptanceCriteriaObject.get("texto").replace(placeHolderMallasMVP, "MVP " + mvpMallasDetectado));
//                    }
//                }
//            }
//            validAcceptanceCriteriaObject.put("texto", validAcceptanceCriteriaObject.get("texto").replace(".", ""));
//
//            message = String.format("Es válido %s", acceptanceCriteria);
//            isValid = true;
//
//            int differences = findChangedWords(validAcceptanceCriteriaObject.get("texto"), acceptanceCriteria).size();
//
//            if (differences >= numeroDiferenciasMaxError) {
//                message = String.format("No es válido %s", acceptanceCriteria);
//                message += errorTemplateCriterioAceptacionText;
//                isValid = tipoIncidenciaKey.isEmpty();
//                isWarning = !tipoIncidenciaKey.isEmpty();
            }
        }else {
            message = "Sin Criterio de Aceptación";
            //message += errorTemplateCriterioAceptacionText;
            isValid = false;
        }

        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationTeamAssigned(String tipoDesarrollo, boolean validacionEnvioFormulario, String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        String jiraTicketStatus = jiraTicketResult.getAsJsonObject("status").get("name").getAsString();
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
        teamFieldLabelByIssueType.put("Dependency", dependencyMap);
        String issueType = jiraTicketResult.getAsJsonObject("fields")
                .getAsJsonObject("issuetype")
                .get("name").getAsString();

        String currentTeamFieldField = (teamFieldLabelByIssueType.containsKey(issueType)) ? teamFieldLabelByIssueType.get(issueType).get("field") : "";

        List<String> estadosExtraMallasHost = Arrays.asList("Ready", "Test", "Ready To Verify");
        List<String> statusTableroDQA = Arrays.asList("Ready", "In Progress", "Test", "Ready To Verify", "Ready To Deploy", "Deployed", "Accepted"); //puede ser variable global



        Object currentTeam = null;
        if (!currentTeamFieldField.equals("")) {
            currentTeam = this.jiraTicketResult.get(currentTeamFieldField) == null ? null : this.jiraTicketResult.get(currentTeamFieldField);
        }

        if (currentTeam != null && ((HashMap)currentTeam).get("id").equals(teamIdDQA)) {
            this.isInTableroDQA = true;
            message = "Asignado a Tablero de DQA";
            isValid = true;
        } else {
            message = "No está en el Tablero de DQA";
            List<String> statusTableroDQANew = statusTableroDQA;
            if (statusTableroDQANew.contains(jiraTicketStatus)) {
                if (validacionEnvioFormulario) {
                    message += "Atención: No olvidar que para regresar el ticket a DQA, se debe cambiar el estado del ticket y la Subtarea DQA";

                    if (tipoDesarrollo.equals("Mallas") || tipoDesarrollo.equals("HOST")) {
                        message += String.join(", ", estadosExtraMallasHost);
                    } else {
                        message += "Ready";
                    }
                }
                isValid = false;
            }
        }

        return getValidationResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    public Map<String, Object> getValidationValidateJIRAStatus(String tipoDesarrollo, String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        //String tipoDesarrollo = "";

        var jiraTicketStatus = jiraTicketResult
                .getAsJsonObject("fields")
                .getAsJsonObject("status")
                .get("name").getAsString();
        List<String> estadosExtraMallasHost = Arrays.asList("Ready", "Test", "Ready To Verify");
        List<String> statusTableroDQA = Arrays.asList(
                "Ready",
                "In Progress",
                "Test",
                "Ready To Verify",
                "Ready To Deploy",
                "Deployed",
                "Accepted"
        );

        if (tipoDesarrollo.equals("Mallas") || tipoDesarrollo.equals("HOST")) {
            statusTableroDQA.addAll(estadosExtraMallasHost);
        }

        List<String> statusList = statusTableroDQA;

        message = String.format("Con estado %s", jiraTicketStatus);

        if (statusList.contains(jiraTicketStatus)) {
            isValid = true;

            List<String> listaEstados = new ArrayList<>(Arrays.asList("Ready", "Deployed"));
            if (tipoDesarrollo.equals("Mallas") || tipoDesarrollo.equals("HOST")) {
                listaEstados.addAll(estadosExtraMallasHost);
            }

            if (!listaEstados.contains(jiraTicketStatus)) { //isInTableroDQA debe ser enviado para
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


}
