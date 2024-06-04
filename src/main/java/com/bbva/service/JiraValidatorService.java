package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.JiraResDTO;

import java.util.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.List;
import java.util.Map;

public class JiraValidatorService {
    private static final Logger LOGGER = Logger.getLogger(JiraValidatorService.class.getName());
    private JiraApiService jiraApiService;
    private boolean isValidURL;
    private List<Map<String, Object>> jiraTicketResult;
    private String jiraCode;
    private String jiraPADCode;
    private List<String> validPADList = Arrays.asList("pad3", "pad5");
    private String boxClassesBorder;
    private String tipoDesarrollo;
    private String tipoDesarrolloFormulario;
    private final String ticketVisibleLabel = "Ticket";

    //Todas la reglas de negocio
    public IDataResult<JiraResDTO> getValidatorByUrl(JiraValidatorByUrlRequest dto) throws Exception { //username, token
        jiraApiService = new JiraApiService(dto.getUserName(), dto.getToken());
        //jiraApiService.testConnection();

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
                jiraTicketResult = results;
                System.out.println(jiraTicketResult);
            }
            //return new SuccessDataResult<>(new JiraValidatorByUrlResponse("OK"), "CONEXION EXITOSA");
        }

        List<Map<String, Object>> results = getResults();
        List<JiraResDTO> jiraResDTOList = new ArrayList<>();

        for (Map<String, Object> result : results) {
            JiraResDTO jiraResDTO = new JiraResDTO();
            jiraResDTO.setIsValid((String) result.get("isValid"));
            jiraResDTO.setIsWarning((String) result.get("isWarning"));
            jiraResDTO.setHelpMessage((String) result.get("helpMessage"));
            jiraResDTO.setGroup((String) result.get("group"));
            jiraResDTOList.add(jiraResDTO);
        }
        LOGGER.log(null, "DTORESPONSE: " + jiraResDTOList.toString());
        return new SuccessDataResult<>( null, "CONEXION EXITOSA");


    }
    public List<Map<String,Object>> getResults() {
        List<Map<String,Object>> res = new ArrayList<>();
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
                res.add(Map.of("message", "No se encontraron errores", "isValid", true, "isWarning", false, "helpMessage", "", "group", "Ticket"));
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


    //------------------- REGLA DE NEGOCIO 1-------------------
    public Map<String, Object> getValidationURLJIRA(String helpMessage, String group) {
        String message;
        boolean isValid;
        boolean isWarning = false;


        String[] jiraCodeParts = jiraCode.split("-");
        String jiraPADCode = jiraCodeParts[0].toUpperCase();

        if (validPADList.contains(jiraPADCode.toLowerCase())) {
            message = "Se encontr&oacute; <div class=\"" + boxClassesBorder + "\">" + jiraPADCode + "</div>";
            isValid = true;
        } else {
            message = "No encontr&oacute;  <div class=\"" + boxClassesBorder + "\">" + String.join(" o ", validPADList) + "</div>";
            isValid = false;
        }

        return Map.of("message", message, "isValid", isValid, "isWarning", isWarning, "helpMessage", helpMessage, "group", group);
    }

    //------------------- REGLA DE NEGOCIO 2-------------------
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

        String summaryComparacion = ((String) jiraTicketResult.get(0).get("summary")).toLowerCase();

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
            message += "<div class='" + boxClassesBorder + "'><strong>Atenci&oacute;n</strong>:<br> El summary es: <div class=\"" + boxClassesBorder + " border-dark\">" + jiraTicketResult.get(0).get("summary") + "</div></div>";
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

    //------------------- REGLA DE NEGOCIO 3-------------------
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
}
