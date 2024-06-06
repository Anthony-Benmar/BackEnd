package com.bbva.util.ApiJiraMet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.*;

public class ValidationUrlJira {
    private  String jiraCode ;
    private final String validPADList = "pad3,pad5";
    private final String ticketGroup = "Ticket";
    private JsonObject jiraTicketResult;

    public ValidationUrlJira(String jiraCode, JsonObject jiraTicketResult) {
        this.jiraCode = jiraCode;
        this.jiraTicketResult = jiraTicketResult;
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

        Map<String, List<String>> tipoDesarrolloBySummaryObject = new HashMap<>();
        tipoDesarrolloBySummaryObject.put("Mallas", Arrays.asList("Control M"));
        tipoDesarrolloBySummaryObject.put("HOST", Arrays.asList("host"));
        tipoDesarrolloBySummaryObject.put("Hammurabi", Arrays.asList("hammurabi"));
        tipoDesarrolloBySummaryObject.put("MigrationTool", Arrays.asList("MigrationTool"));
        tipoDesarrolloBySummaryObject.put("SmartCleaner", Arrays.asList("smartcleaner"));
        tipoDesarrolloBySummaryObject.put("Ingesta", Arrays.asList("ingesta", "kirby"));
        tipoDesarrolloBySummaryObject.put("Procesamiento", Arrays.asList("procesamiento"));
        tipoDesarrolloBySummaryObject.put("Operativizacion", Arrays.asList("operativizacion"));
        tipoDesarrolloBySummaryObject.put("Productivizacion", Arrays.asList("productivizacion"));
        tipoDesarrolloBySummaryObject.put("Scaffolder", Arrays.asList("assets"));
        tipoDesarrolloBySummaryObject.put("SparkCompactor", Arrays.asList("sparkcompactor"));
        tipoDesarrolloBySummaryObject.put("JSON Global", Arrays.asList("json"));
        tipoDesarrolloBySummaryObject.put("Teradata", Arrays.asList("Creación de archivo"));

        String summaryComparacion = jiraTicketResult.get("fields").getAsJsonObject().get("summary").toString().toLowerCase();

        for (Map.Entry<String, List<String>> entry : tipoDesarrolloBySummaryObject.entrySet()) {
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
//    public Map<String, Object> getValidatorValidateHUTType(String helpMessage, String tipoDesarrolloSummary, String group) {
//        String message = "";
//        boolean isValid = false;
//        boolean isWarning = false;
//
//        if (jiraPADCode.equals("PAD3") || jiraPADCode.equals("PAD5")) {
//            if (!tipoDesarrolloSummary.isEmpty()) {
//                tipoDesarrollo = tipoDesarrolloSummary;
//                if (tipoDesarrolloFormulario.toLowerCase().contains("scaffolder") && !tipoDesarrolloFormulario.toLowerCase().contains("despliegue")) {
//                    tipoDesarrollo = "Scaffolder";
//                }
//                //message = String.format("<div class=\"%s\">Tipo de desarrollo</div> es <div class=\"%s bg-dark border border-dark\">%s</div>", boxClassesBorder, boxClassesBorder, tipoDesarrollo);
//                message = "Tipo de desarrollo: " + tipoDesarrollo + " valido para el formulario";
//                isValid = true;
//            } else {
//                message = String.format("No se pudo detectar el <div class=\"%s\">Tipo de desarrollo</div>", boxClassesBorder);
//            }
//        } else {
//            message = String.format("El código JIRA '%s' no es válido para validar el Tipo de desarrollo", jiraPADCode);
//        }
//
//        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
//    }

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

    public Map<String, Object> getValidatorValidateHUTType(String helpMessage, String tipoDesarrolloSummary, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;
        ArrayList<String> projectCodeList = new ArrayList<>(Arrays.asList("PAD3", "PAD5"));

        if (projectCodeList.contains(this.jiraCode) && tipoDesarrolloSummary != ""){
            var tipoDesarrollo = tipoDesarrolloSummary;
            //VALIDA REGISTRO EN EL FORMULARIO SCALPY
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
//    public Map<String, Object> getValidationAcceptanceCriteria(String helpMessage, String group) {
//        String message = "";
//        boolean isValid = false;
//        boolean isWarning = false;
//        String tipoDesarrollo = "";
//
//        Map<String, String> acceptanceCriteriaReplacements = new HashMap<>();
//        acceptanceCriteriaReplacements.put("*", "");
//        acceptanceCriteriaReplacements.put("_MVP_", "MVP");
//        acceptanceCriteriaReplacements.put(".", "");
//        acceptanceCriteriaReplacements.put("\"", "");
//        acceptanceCriteriaReplacements.put("“", "");
//        acceptanceCriteriaReplacements.put("”", "");
//
//        String acceptanceCriteria = multiReplace(jiraTicketResult.get("acceptanceCriteria").toString(), acceptanceCriteriaReplacements);
//        Map<String, Object> validAcceptanceCriteriaObject = acceptanceCriteriaByTipoDesarrollo.get(tipoDesarrollo);
//        String errorTemplateCriterioAceptacionText = String.format("Atención<br>Debe tener un formato similar a: %s", validAcceptanceCriteriaObject.get("texto"));
//
//        if (!acceptanceCriteria.isEmpty()) {
//            if (tipoDesarrollo.equals("Mallas") || tipoDesarrollo.equals("HOST")) {
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
//            }
//        } else {
//            message = "Sin Criterio de Aceptación";
//            message += errorTemplateCriterioAceptacionText;
//            isValid = false;
//        }
//
//        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
//    }
//    public Map<String, Object> getValidationValidateSubTaskValidateContractor(Map<String, Object> subtaskObject, String subTaskLabel, Map<String, Object> subTaskAcceptedRow, String helpMessage, String group) {
//        String message = "";
//        boolean isValid = false;
//        boolean isWarning = false;
//
//        if (!subTaskAcceptedRow.get("email").toString().contains(".contractor")) {
//            message = String.format("%s es Interno BBVA", subTaskAcceptedRow.get("email"));
//            isValid = true;
//        } else {
//            message = String.format("Subtarea %s no es Interno BBVA", subTaskLabel);
//            isValid = false;
//        }
//
//        if (subtaskObject.containsKey("messagePrefix")) {
//            message = String.format("%s %s", subtaskObject.get("messagePrefix"), message);
//        }
//
//        return getValidatonResultsDict(message, isValid, isWarning, helpMessage, group);
//    }


    public Map<String, Object> getValidationTeamAssigned(boolean validacionEnvioFormulario, String helpMessage, String group) {
        String message = "";
        boolean isValid = false;
        boolean isWarning = false;
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
        if ((teamFieldLabelByIssueType.containsKey(issueType))){
            System.out.println(teamFieldLabelByIssueType.containsKey(issueType));
        }
        var currentTeam = (jiraTicketResult.get(currentTeamFieldField) == null) ? null : (Map<String, Object>) jiraTicketResult.get(currentTeamFieldField);

        jiraTicketResult.getAsJsonObject("fields").getAsJsonObject("issuetype").get("name").getAsString();

        JsonArray subtasks = jiraTicketResult
                .getAsJsonObject("fields")
                .getAsJsonArray("subtasks");

        subtasks.forEach(subtask -> {
            subtask.getAsJsonObject()
                    .getAsJsonObject("fields")
                    .getAsJsonObject("status")
                    .get("name").getAsString();
        });


        String teamIdDQA = "2461905";
        Boolean isInTableroDQA = false;
        String jiraTicketStatus = jiraTicketResult.getAsJsonObject("status").get("name").getAsString();
        String tipoDesarrollo = "";

        List<String> estadosExtraMallasHost = Arrays.asList("Ready", "Test", "Ready To Verify");
        List<String> statusTableroDQA = Arrays.asList("Ready", "In Progress", "Test", "Ready To Verify", "Ready To Deploy", "Deployed", "Accepted");

        if (currentTeam != null && currentTeam.get("id").equals(teamIdDQA)) {
            isInTableroDQA = true;
            message = "Asignado a Tablero de DQA";
            isValid = true;
        } else {
            message = "No está en el Tablero de DQA";
            if (statusTableroDQA.contains(jiraTicketStatus)) {
                if (validacionEnvioFormulario) {
                    message += "Atención: No olvidar que para regresar el ticket a DQA, se debe cambiar el estado del ticket y la Subtarea DQA";
                    if (tipoDesarrollo.equals("Mallas") || tipoDesarrollo.equals("HOST")) {
                        message += String.join(", ", estadosExtraMallasHost);
                    } else {
                        message += "Ready";
                    }
                }
            }
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
    public void rule1_documents(){
    }

    public void rule2_documents(){
    }



}
