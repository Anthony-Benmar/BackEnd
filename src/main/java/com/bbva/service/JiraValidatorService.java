package com.bbva.service;

import com.bbva.core.HandledException;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.JiraMessageResponseDTO;
import com.bbva.dto.jira.response.JiraResDTO;
import com.bbva.dto.jira.response.JiraResponseDTO;
import com.bbva.util.ApiJiraMet.ValidationUrlJira;
import com.bbva.util.ApiJiraMet.ValidatorValidateSummaryHUTType;
import com.bbva.util.ApiJiraName;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.http.HttpClient;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;


public class JiraValidatorService {
    private static final Logger LOGGER = Logger.getLogger(JiraValidatorService.class.getName());
    private JiraApiService jiraApiService;
    private String jiraPADCode;
    private List<String> validPADList = Arrays.asList("pad3", "pad5");
    private String boxClassesBorder;
    private String tipoDesarrollo;
    private String tipoDesarrolloFormulario;
    private final String ticketVisibleLabel = "Ticket";
    private HttpClient httpClient;
    private CookieStore cookieStore = new BasicCookieStore();
    //Map<String, String> customFields = new HashMap<>();

    private ValidationUrlJira validationUrlJira;
    private ValidatorValidateSummaryHUTType validatorValidateSummaryHUTType;

    //Todas la reglas de negocio
    public IDataResult<JiraResponseDTO> getValidatorByUrl(JiraValidatorByUrlRequest dto) throws Exception {
        JiraResponseDTO jiraResponseDTO = new JiraResponseDTO();
        List<JiraMessageResponseDTO> messages = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;
        int warningCount = 0;
        this.jiraApiService = new JiraApiService();
        this.httpClient = HttpClient.newHttpClient();
        String acceptanceCriteriaGroup = "Criterio de Aceptacion";

        var isValidUrl = validateJiraFormatURL(dto.getUrlJira());

        var issuesMetadada = getMetadataIssues(dto);
        var jsonResponse = JsonParser.parseString(issuesMetadada).getAsJsonObject();
        var jiraTicketResult = jsonResponse.getAsJsonArray("issues").get(0).getAsJsonObject();



        if (issuesMetadada.isEmpty() && jiraTicketResult !=null){
            throw new HandledException("500", "no existe datos del ticket jira");
        }


        ArrayList<Map<String, Object>> result_final = new ArrayList<>();
        int ruleIdCounter = 1;

        var instancesRules = new ValidationUrlJira(dto.getUrlJira(), jiraTicketResult);
        var result_1 = instancesRules.getValidatorProjectPAD("Validar que sea PAD3 o PAD5", "Ticket");
        var result_2 = instancesRules.getValidatorValidateSummaryHUTType("Validar el tipo de desarrollo en el summary", "Ticket");
        var tipoDesarrollo = result_2.get("tipoDesarrolloSummary").toString();
        var result_3 = instancesRules.getValidatorValidateHUTType("Detectar el tipo de desarrollo por el prefijo y el summary", result_2.get("tipoDesarrolloSummary").toString(), "Ticket");
        var result_4 = instancesRules.getValidatorIssueType("Validar que el Issue type sea Story o Dependency", "Ticket");

        var result_5 = instancesRules.getValidatorDocumentAttachByDevType(tipoDesarrollo);


        var result_10 = instancesRules.getValidationValidateSubTaskStatus(tipoDesarrollo,"Se valida que la subtarea tenga el Status correcto", "Subtarea");
        var result_11 = instancesRules.getValidationValidateSubTaskValidateContractor(dto,"Se valida la subtarea: El email debe pertenecer a un Usuario de Negocio Interno BBVA", "Subtarea");
        var result_12 = instancesRules.getValidationAcceptanceCriteria("Validar el criterio de aceptacion, segun el tipo de desarrollo debe ser similar a la plantilla", acceptanceCriteriaGroup);
        var result_13 = instancesRules.getValidationTeamAssigned(tipoDesarrollo,true,"Validar que el equipo asignado sea el correcto", "Ticket");
        var result_14 = instancesRules.getValidationValidateJIRAStatus(tipoDesarrollo,"Validar el Status de Ticket JIRA","Ticket");

        result_final.add(result_1);
        result_final.add(result_2);
        result_final.add(result_3);
        result_final.add(result_4);
        result_final.add(result_5);

        result_final.add(result_10);
        result_final.add(result_11);
        result_final.add(result_12);
        result_final.add(result_13);
        result_final.add(result_14);



        for (Map<String, Object> result : result_final) {
            JiraMessageResponseDTO message = new JiraMessageResponseDTO();
            message.setRuleId(ruleIdCounter++);
            switch (message.getRuleId()) {
                case 1:
                    message.setRule("ValidationURLJIRA");
                    break;
                case 2:
                    message.setRule("ValidatorValidateSummaryHUTType");
                    break;
                case 3:
                    message.setRule("getValidatorValidateHUTType");
                    break;
                case 4:
                    message.setRule("ValidatorIssueType");
                    break;
                case 5:
                    message.setRule("ValidatorDocumentAttachByDevType");
                    break;
                case 6:
                    message.setRule("ValidationValidateSubTaskStatus");
                    break;
                case 7:
                    message.setRule("ValidationValidateSubTaskValidateContractor");
                    break;
                case 8:
                    message.setRule("ValidationAcceptanceCriteria");
                    break;
                case 9:
                    message.setRule("ValidationTeamAssigned");
                    break;
                case 10:
                    message.setRule("ValidationValidateJIRAStatus");
                    break;
                default:
                    message.setRule("Unknown");
                    break;
            }
            message.setMessage((String) result.get("message"));
            if ((Boolean) result.get("isWarning")) {
                message.setStatus("warning");
                warningCount++;
            } else if ((Boolean) result.get("isValid")) {
                message.setStatus("success");
                successCount++;
            } else {
                message.setStatus("error");
                errorCount++;
            }
            messages.add(message);
        }

        jiraResponseDTO.setData(messages);
        jiraResponseDTO.setSuccessCount(successCount);
        jiraResponseDTO.setErrorCount(errorCount);
        jiraResponseDTO.setWarningCount(warningCount);

//        var url = ApiJiraName.URL_API_JIRA_SQL + this.query + jiraApiService.getQuerySuffixURL();
//        var resultado = jiraApiService.GetJiraAsync(dto.getUserName(),dto.getToken(),url);

        return new SuccessDataResult<>(jiraResponseDTO, "Reglas de validacion");
    }

    public String getMetadataIssues(JiraValidatorByUrlRequest dto) throws Exception {

        //String jiraCode = dto.getUrlJira().split("/")[dto.getUrlJira().split("/").length - 1];
        //var tickets = List.of(jiraCode);
        var tickets = List.of(dto.getUrlJira());

        var query = "key%20in%20(" + String.join(",", tickets) + ")";

        var url = ApiJiraName.URL_API_JIRA_SQL + query + this.jiraApiService.getQuerySuffixURL();
        String result = this.jiraApiService.GetJiraAsync(dto.getUserName(), dto.getToken() ,url);
        return result;
    }

    /*public List<Map<String,Object>> getResults() {
        List<Map<String,Object>> res = new ArrayList<>();
        boolean isWithError = false;

        try {
            // to prevent invalid urls sent directly to the server
            if (jiraTicketResult != null && isValidURL) {
                String ticketGroup = "Ticket";
                Map<String, Object> validationURLJiraResult = validationUrlJira.getValidationURLJIRA("Validar que sea PAD3 o PAD5", ticketGroup);

                if (!(Boolean) validationURLJiraResult.get("isValid")) {
                    res.add(validationURLJiraResult);
                } else {
                    //Map<String, Object> validacionEnvioFormulario = getValidatorValidateSentToTablero05("Validar envio de formulario", ticketGroup); // validar a través de un google sheet o BD???
                    //res.add(validacionEnvioFormulario);
                    //Map<String, Object> validacionSummaryResult = getValidatorValidateSummaryHUTType("Validar el tipo de desarrollo en el summary", ticketGroup);
                    //String tipoDesarrolloSummary = (String) validacionSummaryResult.get("tipoDesarrolloSummary");

//                    Map<String, Object> validacionTipoDesarrolloResult = getValidatorValidateHUTType(
//                            "Detectar el tipo de desarrollo por el prefijo de " + ticketVisibleLabel + " y el summary",
//                            tipoDesarrolloSummary,
//                            ticketGroup
//                    );
                }
            }
        }
        finally {
            if (res.isEmpty()) {
                res.add(Map.of("message", "No se encontraron errores", "isValid", true, "isWarning", false, "helpMessage", "", "group", "Ticket"));
            }
        }
        return res;
    }*/

    public boolean validateJiraFormatURL(String jiraURL) {
        String regexPattern = "^(?:https://jira.globaldevtools.bbva.com/(?:browse/)?(?:plugins/servlet/mobile#issue/)?)?([a-zA-Z0-9]+-[a-zA-Z0-9]+)$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(jiraURL.toLowerCase());
        return matcher.matches();
    }


    //------------------- REGLA DE NEGOCIO 1-------------------


    //------------------- REGLA DE NEGOCIO 2-------------------


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

/*
        jiraApiService = new JiraApiService(dto.getUserName(), dto.getToken());
        //jiraApiService.testConnection();




        if (!isValidURL) {
            System.out.println("CONEXION FALLIDA");
            return new SuccessDataResult<>(null, "CONEXION FALLIDA");
        }

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

        List<Map<String, Object>> results2 = getResults();
        List<JiraResDTO> jiraResDTOList = new ArrayList<>();

        for (Map<String, Object> result : results2) {
            JiraResDTO jiraResDTO = new JiraResDTO();
            jiraResDTO.setIsValid((String) result.get("isValid"));
            jiraResDTO.setIsWarning((String) result.get("isWarning"));
            jiraResDTO.setHelpMessage((String) result.get("helpMessage"));
            jiraResDTO.setGroup((String) result.get("group"));
            jiraResDTOList.add(jiraResDTO);
        }
        LOGGER.log(null, "DTORESPONSE: " + jiraResDTOList.toString());
        */