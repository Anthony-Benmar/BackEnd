package com.bbva.util.ApiJiraMet;

import com.bbva.common.jiraValidador.JiraValidatorConstantes;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.service.JiraApiService;
import com.bbva.util.ApiJiraName;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.bbva.common.jiraValidador.JiraValidatorConstantes.*;

public class ValidationUrlJira {
    private  String jiraCode ;
    private final List<String> validPAD = Arrays.asList("pad3", "pad5");
    private JsonObject jiraTicketResult;
    private boolean isInTableroDQA;
    private boolean isEnviadoFormulario;
    private String featureLink;
    private String impactLabel;
    private String coordinationMessage = "de ser necesario coordinar con el <strong>SM / QE</strong>";
    private String currentQ = "2024-Q2";

    public ValidationUrlJira(String jiraCode, JsonObject jiraTicketResult) {
        this.jiraCode = jiraCode;
        this.jiraTicketResult = jiraTicketResult;
        this.isInTableroDQA = false;
        this.isEnviadoFormulario = false;
        JsonElement featureLinkElement = this.jiraTicketResult.get("fields").getAsJsonObject().get("customfield_10004");
        this.featureLink = featureLinkElement.isJsonNull() ? null : featureLinkElement.getAsString();
        JsonElement impactLabelElement = this.jiraTicketResult.get("fields").getAsJsonObject().get("customfield_10267");
        this.impactLabel = impactLabelElement.isJsonNull() ? null : impactLabelElement.getAsString();

    }

    public  Map<String, Object> getValidatorProjectPAD(String helpMessage, String group) {
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

    Map<String,Object> branchPRObject = new HashMap<>();
    branchPRObject.put("branch","");
    branchPRObject.put("status","");

    if (jiraTicketResultPrs.get("prs") != null) {
        cantidadPRs = jiraTicketResultPrs.get("prs").getAsJsonArray().size();
        // por defecto solo deberia venir una pr, asi que se tomara la primera
        if (cantidadPRs > 0) {
            branchPRObject.put("branch",jiraTicketResultPrs.get("prs").getAsJsonArray().get(0).getAsJsonObject().get("destinyBranch").getAsString());
            branchPRObject.put("status",jiraTicketResultPrs.get("prs").getAsJsonArray().get(0).getAsJsonObject().get("status").getAsString());
        }
        // se obtienen las urls de las PRs
        for (JsonElement prObj : jiraTicketResultPrs.get("prs").getAsJsonArray()) {
            Map<String, Object> pr = new HashMap<>();
            pr.put("url", prObj.getAsJsonObject().get("url").getAsString());
            prsUrls.add(pr.get("url").toString());
        }
    }

    if (tipoDesarrollo.equals("PRs") || tipoDesarrollo.equals("Mallas") || tipoDesarrolloPRs.contains(tipoDesarrollo)) {
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
                            message.set("Subtarea no aceptada: " + subTask.getAsJsonObject().get("key").getAsString());
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

    //Ruler OK
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

    //FALTA
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
        teamFieldLabelByIssueType.put("Dependency", dependencyMap);
        String issueType = jiraTicketResult.getAsJsonObject("fields")
                .getAsJsonObject("issuetype")
                .get("name").getAsString();

        String currentTeamFieldField = (teamFieldLabelByIssueType.containsKey(issueType)) ? teamFieldLabelByIssueType.get(issueType).get("field") : "";

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
            statusTableroDQA.replaceAll(String::toLowerCase);
            List<String> statusTableroDQANew = statusTableroDQA;
            if (statusTableroDQANew.contains( jiraTicketStatus.trim().toLowerCase())) {
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
//OK
    public Map<String, Object> getValidationFeatureLinkStatus(JiraValidatorByUrlRequest dto, String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        List<String> validStatuses = Arrays.asList("In Progress", "Test", "Ready To Verify", "Ready To Deploy", "Deployed");

        var query = "key%20in%20(" + String.join(",", featureLink) + ")";
        JsonObject metaData = null;
        try {
            var url = ApiJiraName.URL_API_JIRA_SQL + query + new JiraApiService().getQuerySuffixURL();
            var response = new JiraApiService().GetJiraAsync(dto.getUserName(),dto.getToken(),url);
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

        return this.getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
    }
//OK
    public Map<String, Object> getValidationFeatureLinkProgramIncrement(JiraValidatorByUrlRequest dto, String helpMessage, String group){
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        var query = "key%20in%20(" + String.join(",", featureLink) + ")";
        JsonObject metaData = null;
        try {
            var url = ApiJiraName.URL_API_JIRA_SQL + query + new JiraApiService().getQuerySuffixURL();
            var response = new JiraApiService().GetJiraAsync(dto.getUserName(),dto.getToken(),url);
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

        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
    }
//Pendiente
//    public Map<String, Object> getValidationValidateSubTask(Map<String, Object> subtaskObject, String helpMessage, String group) {
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
//Por revisar
    public Map<String, Object> getValidationValidateImpactLabel(String helpMessage, String group, String tipoDesarrollo) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;

        List<String> validImpactLabel = Arrays.asList("AppsInternos", "Datio");
        List<String> validImpactLabelListHost = Arrays.asList("DataHub", "Host", "Plataforma_InformacionalP11");

        List<String> validImpactLabelFinalList = tipoDesarrollo.equals("HOST") ? validImpactLabelListHost : validImpactLabel;

        List<String> impactLabelNotExistsList = new ArrayList<>();


        List<String> jiraTicketImpactLabelList = impactLabel == null ? new ArrayList<>() : Collections.singletonList(impactLabel);


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

        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
    }
//Por revisar
    public Map<String, Object> getValidationFixVersion(String helpMessage, String group) {
        String message = "";
        boolean isValid;
        boolean isWarning = false;

        String[] jiraCodeParts = this.jiraCode.split("-");
        String jiraPADCode = jiraCodeParts[0].toUpperCase();

        JsonArray fixVersions = this.jiraTicketResult.getAsJsonObject("fields").getAsJsonArray("fixVersions");

        if (!fixVersions.isEmpty()) {
            String fixVersionURLPrefix = ApiJiraName.URL_API_BASE + "/issues?jql=project=" + jiraPADCode + " AND fixVersion=";

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

        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
    }

    //    public Map<String, Object> getValidationValidateJIRAStatus(String helpMessage, String group) {
//        String message = "";
//        boolean isValid = false;
//        boolean isWarning = false;
//
//        List<String> statusList = new ArrayList<>(statusTableroDQA);
//
//        if (tipoDesarrollo.equals("Mallas") || tipoDesarrollo.equals("HOST")) {
//            statusList.addAll(estadosExtraMallasHost);
//        }
//
//        message = "Con estado <div class='" + boxClassesBorder + "'>" + jiraTicketStatus + "</div>";
//
//        if (statusList.contains(jiraTicketStatus)) {
//            isValid = true;
//
//            List<String> listaEstados = new ArrayList<>();
//            listaEstados.add("Ready");
//            listaEstados.add("Deployed");
//            if (tipoDesarrollo.equals("Mallas") || tipoDesarrollo.equals("HOST")) {
//                listaEstados.addAll(estadosExtraMallasHost);
//            }
//
//            if (!listaEstados.contains(jiraTicketStatus)) {
//                if (isInTableroDQA && isEnviadoFormulario) {
//                    isValid = true;
//                    isWarning = true;
//                    message += "<div class='" + boxWarningClasses + "'><strong>Atenci&oacute;n</strong>:<br> Es posible que el <div class='" + boxClassesBorder + " border-dark'>" + ticketVisibleLabel + "</div> se encuentre en revisi&oacute;n, recordar que el estado inicial de un <div class='" + boxClassesBorder + " border-dark mt-2'>" + ticketVisibleLabel + "</div> por revisar es <div class='mt-2 " + boxClassesBorder + " border-dark'>Ready</div></div>";
//                } else {
//                    isValid = false;
//                }
//            }
//        }
//
//        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
//    }
}
