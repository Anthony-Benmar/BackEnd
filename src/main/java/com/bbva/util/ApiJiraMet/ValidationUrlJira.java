package com.bbva.util.ApiJiraMet;

import com.bbva.common.jiraValidador.JiraValidatorConstantes;
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

}
