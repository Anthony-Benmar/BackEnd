package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.JiraResDTO;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.List;
import java.util.Map;

public class JiraValidatorService {
    private JiraApiService jiraApiService;
    private boolean isValidURL;
    private Map<String, Object> jiraTicketResult;
    private String jiraCode;
    private String jiraPADCode;
    private List<String> validPADList = Arrays.asList("pad3", "pad5");
    private String boxClassesBorder;
    private String tipoDesarrollo;
    private String tipoDesarrolloFormulario;
    private final String ticketVisibleLabel = "Ticket";

    //Todas la reglas de negocio
    public IDataResult<JiraResDTO> getValidatorByUrl(JiraValidatorByUrlRequest dto) { //username, token
        jiraApiService = new JiraApiService(dto.getUserName(), dto.getToken());
        jiraApiService.testConnection();

        dto.setUrlJira(dto.getUrlJira().toUpperCase());
        validateJiraURL(dto.getUrlJira());
        jiraCode = dto.getUrlJira().split("/")[dto.getUrlJira().split("/").length - 1];
        if (!isValidURL) {

            System.out.println("CONEXION FALLIDA");
            return new SuccessDataResult<>(null, "CONEXION FALLIDA");

        } else {
            System.out.println("CONEXION EXITOSA");
            // Querying Jira API
            List<Map<String, Object>> queryResult = jiraApiService.searchByTicket(List.of(jiraCode),
                    List.of("id", "issuetype", "changelog", "teamId", "petitionerTeamId", "receptorTeamId", "labels", "featureLink", "issuelinks", "status", "summary", "acceptanceCriteria", "subtasks", "impactLabel", "itemType", "techStack",
                            "fixVersions", "attachment", "prs"));
            System.out.println("QUERY RESULT: " + queryResult);
            List<Map<String, Object>> results = queryResult;
            System.out.println("RESULTS: " + results);
            if (results != null && !results.isEmpty()) {
                jiraTicketResult = results.get(0);
                System.out.println(jiraTicketResult);
                //List<Map<String, Object>> attachments = (List<Map<String, Object>>) jiraTicketResult.get("attachment");
//                if (attachments != null) {
//                    for (Map<String, Object> attachment : attachments) {
//                        adjuntos.add((String) attachment.get("filename"));
//                    }
//                }
//                extraTicketResults = __getExtraTicketResults(jiraTicketResult);
//                __detectParentOIssuesTicketType(extraTicketResults.get("parentIssueLinksDeployedTablero05Develop"));
//                featureLinkTicket = (Map<String, Object>) extraTicketResults.get("featureLink");
//                dependencyTicket = (Map<String, Object>) extraTicketResults.get("dependency");
//                issueType = (String) jiraTicketResult.get("issuetype.name");
//                currentTeamFieldLabel = teamFieldLabelByIssueType.containsKey(issueType) ? teamFieldLabelByIssueType.get(issueType).get("label") : "";
//                currentTeamFieldField = teamFieldLabelByIssueType.containsKey(issueType) ? teamFieldLabelByIssueType.get(issueType).get("field") : "";
                //jiraTicketStatus = (String) jiraTicketResult.get("status.name");
            }
            //return new SuccessDataResult<>(new JiraValidatorByUrlResponse("OK"), "CONEXION EXITOSA");
        }
        List<Object> results = getResults();
        List<JiraResDTO> jiraResDTOList = new ArrayList<>();

        for (Object result : results) {
            Map<String, Object> resultMap = (Map<String, Object>) result;
            JiraResDTO jiraResDTO = new JiraResDTO();
            jiraResDTO.setIsValid((String) resultMap.get("isValid"));
            jiraResDTO.setIsWarning((String) resultMap.get("isWarning"));
            jiraResDTO.setHelpMessage((String) resultMap.get("helpMessage"));
            jiraResDTO.setGroup((String) resultMap.get("group"));
            jiraResDTOList.add(jiraResDTO);
        }
return new SuccessDataResult<>( null, "CONEXION EXITOSA");
        //REgla 1
        //Regla_1();
        //REgla 2


//        JiraValidatorByUrlResponse response = new JiraValidatorByUrlResponse("OK");
//        return new SuccessDataResult<>(response, "CONEXION EXITOSA");
    }
    public List<Object> getResults() {
        List<Object> res = new ArrayList<>();
        boolean isWithError = false;

        try {
            // to prevent invalid urls sent directly to the server
            if (jiraTicketResult != null && isValidURL) {
                String ticketGroup = "Ticket";
                Map<String, Object> validationURLJiraResult = getValidationURLJIRA("Validar que sea PAD3 o PAD5", ticketGroup);

                if (!(Boolean) validationURLJiraResult.get("isValid")) {
                    res.add(validationURLJiraResult);
                } else {
                    //Map<String, Object> validacionEnvioFormulario = getValidatorValidateSentToTablero05("Validar envio de formulario", ticketGroup); // validar a través de un google sheet o BD???
                    //res.add(validacionEnvioFormulario);
                    Map<String, Object> validacionSummaryResult = getValidatorValidateSummaryHUTType("Validar el tipo de desarrollo en el summary", ticketGroup);
                    String tipoDesarrolloSummary = (String) validacionSummaryResult.get("tipoDesarrolloSummary");

                    Map<String, Object> validacionTipoDesarrolloResult = getValidatorValidateHUTType(
                            "Detectar el tipo de desarrollo por el prefijo de " + ticketVisibleLabel + " y el summary",
                            tipoDesarrolloSummary,
                            ticketGroup
                    );
                }
            }
        }
        finally {
            if (res.isEmpty()) {
                res.add(new SuccessDataResult<>("OK", "CONEXION EXITOSA"));
            }
        }
        return res;
    }

    public void validateJiraURL(String jiraURL) {
        String regexPattern = "^(?:https://jira.globaldevtools.bbva.com/(?:browse/)?(?:plugins/servlet/mobile#issue/)?)?([a-zA-Z0-9]+-[a-zA-Z0-9]+)$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(jiraURL.toLowerCase());
        this.isValidURL = matcher.matches();
    }

    public Map<String, Object> getValidationURLJIRA(String helpMessage, String group) {
        String message;
        boolean isValid;
        boolean isWarning = false;

        if (validPADList.contains(jiraPADCode.toLowerCase())) {
            message = "Se encontr&oacute; <div class=\"" + boxClassesBorder + "\">" + jiraPADCode + "</div>";
            isValid = true;
        } else {
            message = "No encontr&oacute;  <div class=\"" + boxClassesBorder + "\">" + String.join(" o ", validPADList) + "</div>";
            isValid = false;
        }

        return Map.of("message", message, "isValid", isValid, "isWarning", isWarning, "helpMessage", helpMessage, "group", group);
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

    public Map<String, Object> getValidatorValidateHUTType(String helpMessage, String tipoDesarrolloSummary, String group) {
        String message;
        boolean isValid;
        boolean isWarning = false;

        if ((jiraPADCode.equals("PAD3") || jiraPADCode.equals("PAD5")) && !tipoDesarrolloSummary.isEmpty()) {
            this.tipoDesarrollo = tipoDesarrolloSummary;
            if (this.tipoDesarrolloFormulario.toLowerCase().contains("scaffolder") && !this.tipoDesarrolloFormulario.toLowerCase().contains("despliegue")) {
                this.tipoDesarrollo = "Scaffolder";
            }

            message = "<div class=\"" + boxClassesBorder + "\">Tipo de desarrollo</div> es <div class=\"" + boxClassesBorder + " bg-dark border border-dark\">" + this.tipoDesarrollo + "</div>";
            isValid = true;
        } else {
            message = "No se pudo detectar el <div class=\"" + boxClassesBorder + "\">Tipo de desarrollo</div>";
            isValid = false;
        }
        return Map.of("message", message, "isValid", isValid, "isWarning", isWarning, "helpMessage", helpMessage, "group", group);
    }
    //MEtodos de las validaciones
    //void Regla_1(){
    // /Cuerpo
    //        }
}
