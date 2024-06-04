package com.bbva.util.ApiJiraMet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidatorValidateSummaryHUTType {
    private Map<String, Object> jiraTicketResult;
    private String boxClassesBorder;

    public ValidatorValidateSummaryHUTType(Map<String, Object> jiraTicketResult, String boxClassesBorder) {
        this.jiraTicketResult = jiraTicketResult;
        this.boxClassesBorder = boxClassesBorder;
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

        String summaryComparacion = ((String) jiraTicketResult.get("summary")).toLowerCase();

        for (Map.Entry<String, List<String>> entry : tipoDesarrolloBySummaryObject.entrySet()) {
            String tipoDesarrolloKey = entry.getKey();
            List<String> tipoDesarrolloItem = entry.getValue();

            if (tipoDesarrolloItem.stream().anyMatch(validacionText -> summaryComparacion.contains(validacionText.toLowerCase()))) {
                tipoDesarrolloSummary = tipoDesarrolloKey;
                break;
            }
        }

        if (!tipoDesarrolloSummary.isEmpty()) {
            message = "<div><div class=\"" + boxClassesBorder + "\">Summary</div> Con <div class=\"" + boxClassesBorder + "\">Tipo de desarrollo</div> v&aacute;lido";
            isValid = true;
        } else {
            message = "<div class=\"" + boxClassesBorder + "\">Summary</div> sin <div class=\"" + boxClassesBorder + "\">Tipo de desarrollo</div> valido";
            message += "<div class='" + boxClassesBorder + "'><strong>Atenci&oacute;n</strong>:<br> El summary es: <div class=\"" + boxClassesBorder + " border-dark\">" + jiraTicketResult.get("summary") + "</div></div>";
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
}
