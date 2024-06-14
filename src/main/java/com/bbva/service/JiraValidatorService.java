package com.bbva.service;

import com.bbva.core.HandledException;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.JiraMessageResponseDTO;
import com.bbva.dto.jira.response.JiraResponseDTO;
import com.bbva.util.ApiJiraMet.ValidationUrlJira;
import com.bbva.util.ApiJiraMet.ValidatorValidateSummaryHUTType;
import com.bbva.util.ApiJiraName;
import com.google.gson.*;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
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
        String prGroup="PR";

        var isValidUrl = validateJiraFormatURL(dto.getUrlJira());

        var issuesMetadada = getMetadataIssues(dto);
        var jsonResponse = JsonParser.parseString(issuesMetadada).getAsJsonObject();
        var jiraTicketResult = jsonResponse.getAsJsonArray("issues").get(0).getAsJsonObject();
        //Añadir PRs al ticket
        var prs = metadataPRs(jiraTicketResult, dto);
        jiraTicketResult.getAsJsonObject("fields").add("prs", JsonParser.parseString(prs).getAsJsonArray());

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

        var result_6 = instancesRules.getValidationFeatureLink("Se valida el tenga un Feature Link asignado", "Feature Link");
        var result_7 = instancesRules.getValidationFeatureLinkPAD3("Validar que el Feature Link, se recomienda que sea PAD3", "Feature Link");
        var result_8 = instancesRules.getValidationFeatureLinkStatus(dto, "Validar el estado Jira del Feature Link", "Feature Link");
        var result_9 = instancesRules.getValidationFeatureLinkProgramIncrement(dto, "Validar que el Feature Link tenga el Program Increment asignado y correcto (Q Actual)", "Feature Link");
        var result_10 = instancesRules.getValidationValidateImpactLabel("Validar que se tengan los Impact Label correctos (Solo Mallas/HOST)","Ticket", tipoDesarrollo);
        var result_11 = instancesRules.getValidationFixVersion("Validar que se tenga Fix Version (Solo Mallas/HOST)","Ticket");

        var result_12 = instancesRules.getValidationValidateSubTaskStatus(tipoDesarrollo,"Se valida que la subtarea tenga el Status correcto", "Subtarea");
        var result_13 = instancesRules.getValidationValidateSubTaskValidateContractor(dto,"Se valida la subtarea: El email debe pertenecer a un Usuario de Negocio Interno BBVA", "Subtarea");
        var result_14 = instancesRules.getValidationAcceptanceCriteria("Validar el criterio de aceptacion, segun el tipo de desarrollo debe ser similar a la plantilla", acceptanceCriteriaGroup);
        var result_15 = instancesRules.getValidationTeamAssigned(tipoDesarrollo,true,"Validar que el equipo asignado sea el correcto", "Ticket");
        var result_16 = instancesRules.getValidationValidateJIRAStatus(tipoDesarrollo,"Validar el Status de Ticket JIRA","Ticket");

        var result_18 = instancesRules.getValidationValidateSubTask(tipoDesarrollo,"Validar la existencia de las subtareas", "Subtask");
        var result_19 = instancesRules.getValidationValidateAttachment(tipoDesarrollo,"Validar la existencia de los adjuntos", "Attachment");

        var result_17 = instancesRules.getValidationPR(tipoDesarrollo, "Validar que se tenga una PR asociada", prGroup);

        result_final.add(result_1);
        result_final.add(result_2);
        result_final.add(result_3);
        result_final.add(result_4);
        result_final.add(result_5);

        //Validaciones Gianfranco
        result_final.add(result_6);
        result_final.add(result_7);
        result_final.add(result_8);
        result_final.add(result_9);
        result_final.add(result_10);
        result_final.add(result_11);
        result_final.add(result_18);
        result_final.add(result_19);

        //Validaciones Juan
        result_final.add(result_12);
        result_final.add(result_13);
        result_final.add(result_14);
        result_final.add(result_15);
        result_final.add(result_16);
        result_final.add(result_17);



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
                    message.setRule("ValidatorValidateHUTType");
                    break;
                case 4:
                    message.setRule("Validar que el Issue type sea Story o Dependency");//("ValidatorIssueType");
                    break;
                case 5:
                    message.setRule("ValidatorDocumentAttachByDevType"); //Falta descripción
                    break;
                case 6:
                    message.setRule("Validacion Feature Link: Se valida que se cuente con un Feature Link Asociado");//("ValidationFeatureLink");
                    break;
                case 7:
                    message.setRule("Validacion Feature Link PAD3: Se valida que el ticket jira del feature link sea PAD3 (Como advertencia)");//("ValidationFeatureLinkPAD3");
                    break;
                case 8:
                    message.setRule("Validacion Feature Link Status: Se valida el status del ticket jira del feature link, para evitar que un feature link en estado new o ready sea enviado (debe estar en in progress)");//("ValidationFeatureLinkStatus");
                    break;
                case 9:
                    message.setRule("Validacion Feature Link Program Increment: Se valida que el program increment del feature link corresponda al Q vigente");//("ValidationFeatureLinkProgramIncrement");
                    break;
                case 10:
                    message.setRule("Validacion Impact Label: Se valida que el ticket JIR cuente con un Impact Label segun el tipo de desarrollo");//("ValidationValidateImpactLabel");
                    break;
                case 11:
                    message.setRule("Validacion Fix Version: Para el caso de mallas y host, se valida que se cuente con un fix version");//("ValidationFixVersion");
                    break;
                case 12:
                    message.setRule("Validacion Subtareas Status: Se valida el status de las subtareas, para evitar tener subtareas en new o ready según corresponda");//("ValidationValidateSubTaskStatus");
                    break;
                case 13:
                    message.setRule("Validacion Subtareas Contractor: Se valida que el VoBo de la subtarea no lo de un correo .contractor");//("ValidationValidateSubTaskValidateContractor");
                    break;
                case 14:
                    message.setRule("Validacion MVP: Se obtiene el MVP del criterio de aceptacion y se valida con el registrado en el excel de lideres");//("ValidationAcceptanceCriteria");
                    break;
                case 15:
                    message.setRule("Validacion Asignacion a Tablero de DQA: Se valida que el Ticket JIRA fuera enviado al tablero de DQA");//("ValidationTeamAssigned");
                    break;
                case 16:
                    message.setRule("Validacion Status JIRA: Se valida que el ticket JIRA no llegue en estados invalidos, como new, discarded, etc");//("ValidationValidateJIRAStatus");
                    break;
                case 18:
                    message.setRule("Validacion Subtareas: Segun el tipo de desarrollo / tipo de ticket, se valida que existan ciertas subtareas");//("ValidationValidateSubTask");
                    break;
                case 19:
                    message.setRule("Validacion de documentos adjuntos: C204, P110, RC");//("ValidationValidateAttachment");
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
    public String metadataPRs(JsonObject jiraTicketResult, JiraValidatorByUrlRequest dto) throws Exception {
        String idTicket = jiraTicketResult.getAsJsonObject().get("id").getAsString();
        var url = ApiJiraName.URL_API_JIRA_PULL_REQUEST + idTicket + "&applicationType=stash&dataType=pullrequest";

        String result = this.jiraApiService.GetPRsAsync(dto.getUserName(), dto.getToken(), url);
        var prsJsonResponse = JsonParser.parseString(result).getAsJsonObject();
        var detailPR = prsJsonResponse
                .getAsJsonArray("detail")
                .get(0).getAsJsonObject()
                .getAsJsonArray("pullRequests");
        List<Map<String,Object>> prs = new ArrayList<>();

        for(JsonElement pr : detailPR){
            JsonObject prDetail = pr.getAsJsonObject();
            List<Map<String,Object>> reviewersList = new ArrayList<>();
            JsonArray reviewersArray = prDetail.getAsJsonArray("reviewers");
            for(JsonElement reviewerElement : reviewersArray){
                JsonObject reviewerObject = reviewerElement.getAsJsonObject();
                Map<String,Object> reviewerMap = new HashMap<>();
                reviewerMap.put("approved",reviewerObject.get("approved").getAsBoolean());
                reviewerMap.put("user",reviewerObject.get("name").getAsString());
                reviewersList.add(reviewerMap);
            }
            Map<String, Object> prMap = new HashMap<>();
            prMap.put("url", prDetail.get("url").getAsString());
            prMap.put("status", prDetail.get("status").getAsString());
            prMap.put("destinyBranch", prDetail.getAsJsonObject("destination").get("branch").getAsString());
            prMap.put("reviewers", reviewersList);
            prs.add(prMap);
        }
        //Transformar la lista de objetos Java a una representación JSON en forma de cadena
        Gson gson = new Gson();
        String resutlPrsString = gson.toJson(prs);
        return resutlPrsString;
    }

    public boolean validateJiraFormatURL(String jiraURL) {
        String regexPattern = "^(?:https://jira.globaldevtools.bbva.com/(?:browse/)?(?:plugins/servlet/mobile#issue/)?)?([a-zA-Z0-9]+-[a-zA-Z0-9]+)$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(jiraURL.toLowerCase());
        return matcher.matches();
    }
}