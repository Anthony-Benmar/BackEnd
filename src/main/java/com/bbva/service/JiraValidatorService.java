package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.JiraMessageResponseDTO;
import com.bbva.dto.jira.response.JiraResDTO;
import com.bbva.dto.jira.response.JiraResponseDTO;
import com.bbva.util.ApiJiraMet.ValidationUrlJira;
import com.bbva.util.ApiJiraMet.ValidatorValidateSummaryHUTType;
import com.bbva.util.ApiJiraName;
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
    private boolean isValidURL;
    //private Map<String, Object> jiraTicketResult;
    private String jiraTicketResult;
    private String jiraCode;
    private String jiraPADCode;
    private List<String> validPADList = Arrays.asList("pad3", "pad5");
    private String boxClassesBorder;
    private String tipoDesarrollo;
    private String tipoDesarrolloFormulario;
    private final String ticketVisibleLabel = "Ticket";
    private HttpClient httpClient;
    private CookieStore cookieStore = new BasicCookieStore();
    Map<String, String> customFields = new HashMap<>();
    private String query;
    private List<String> listaprueba;

    private ValidationUrlJira validationUrlJira;
    private ValidatorValidateSummaryHUTType validatorValidateSummaryHUTType;

    //Todas la reglas de negocio
    public IDataResult<JiraResponseDTO> getValidatorByUrl(JiraValidatorByUrlRequest dto) throws Exception {
        JiraResponseDTO jiraResponseDTO = new JiraResponseDTO();
        List<JiraMessageResponseDTO> messages = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        int alertCount = 0;
        JiraResDTO jiraResDTO = new JiraResDTO();
        jiraApiService = new JiraApiService();
        formato(dto);

        validationUrlJira = new ValidationUrlJira( jiraCode );
        validatorValidateSummaryHUTType = new ValidatorValidateSummaryHUTType(this.jiraTicketResult, boxClassesBorder);

        //var result_final = new ArrayList<>();
        ArrayList<Map<String, Object>> result_final = new ArrayList<>();
        int ruleIdCounter = 1;

        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            jiraApiService.getBasicSession(dto.getUserName(), dto.getToken(), httpClient);

            var result_1 = validationUrlJira.getValidationURLJIRA("Validar que sea PAD3 o PAD5", "Ticket");
            var result_2 = validatorValidateSummaryHUTType.getValidatorValidateSummaryHUTType("Validar el tipo de desarrollo en el summary", "Ticket");

            result_final.add(result_1);
            result_final.add(result_2);
            httpClient.close();

            for (Map<String, Object> result : result_final) {
                JiraMessageResponseDTO message = new JiraMessageResponseDTO();
                message.setRuleId(ruleIdCounter++); // Set ruleId and increment counter
                switch (message.getRuleId()) {
                    case 1:
                        message.setRule("ValidationURLJIRA");
                        break;
                    case 2:
                        message.setRule("ValidatorValidateSummaryHUTType");
                        break;
                    default:
                        message.setRule("Unknown");
                        break;
                }
                message.setMessage((String) result.get("message"));
                if ((Boolean) result.get("isWarning")) {
                    message.setStatus("alert");
                    alertCount++;
                } else if ((Boolean) result.get("isValid")) {
                    message.setStatus("success");
                    successCount++;
                } else {
                    message.setStatus("fail");
                    failCount++;
                }
                messages.add(message);
            }

            jiraResponseDTO.setData(messages);
            jiraResponseDTO.setSuccessCount(successCount);
            jiraResponseDTO.setFailCount(failCount);
            jiraResponseDTO.setAlertCount(alertCount);
        }

//        var url = ApiJiraName.URL_API_JIRA_SQL + this.query + jiraApiService.getQuerySuffixURL();
//        var resultado = jiraApiService.GetJiraAsync(dto.getUserName(),dto.getToken(),url);

        return new SuccessDataResult<>(jiraResponseDTO, "Reglas de validacion");
    }

    public void formato(JiraValidatorByUrlRequest dto) throws Exception {
        dto.setUrlJira(dto.getUrlJira().toUpperCase());
        validateJiraURL(dto.getUrlJira());
        this.jiraCode = dto.getUrlJira().split("/")[dto.getUrlJira().split("/").length - 1];

        this.httpClient = HttpClient.newHttpClient();
        this.listaprueba =  List.of("id", "issuetype", "changelog", "teamId", "petitionerTeamId", "receptorTeamId", "labels", "featureLink", "issuelinks", "status", "summary", "acceptanceCriteria", "subtasks", "impactLabel", "itemType", "techStack",
                "fixVersions", "attachment", "prs");
        var tickets = List.of(this.jiraCode);
        this.query = "key%20in%20(" + String.join(",", tickets) + ")";

        var url = ApiJiraName.URL_API_JIRA_SQL + query + this.jiraApiService.getQuerySuffixURL();
        String resultado = jiraApiService.GetJiraAsync(dto.getUserName(),dto.getToken(),url);

        if (resultado != null && !resultado.isEmpty()) {
            this.jiraTicketResult = resultado;
            System.out.println(jiraTicketResult);
        }
    }

    public List<Map<String,Object>> getResults() {
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
    }

    public void validateJiraURL(String jiraURL) {
        String regexPattern = "^(?:https://jira.globaldevtools.bbva.com/(?:browse/)?(?:plugins/servlet/mobile#issue/)?)?([a-zA-Z0-9]+-[a-zA-Z0-9]+)$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(jiraURL.toLowerCase());
        this.isValidURL = matcher.matches();
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