package com.bbva.service;

import com.bbva.core.HandledException;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.InfoJiraProjectDao;
import com.bbva.dao.JiraValidatorLogDao;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.JiraMessageResponseDTO;
import com.bbva.dto.jira.response.JiraResponseDTO;
import com.bbva.entities.jiravalidator.InfoJiraProject;
import com.bbva.entities.jiravalidator.JiraValidatorLogEntity;
import com.bbva.util.ApiJiraMet.JiraValidationMethods;
import com.bbva.util.ApiJiraName;
import com.google.gson.*;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import java.net.http.HttpClient;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class JiraValidatorService {
    private static final Logger LOGGER = Logger.getLogger(JiraValidatorService.class.getName());
    private JiraApiService jiraApiService;
    private final JiraValidatorLogDao jiraValidatorLogDao = new JiraValidatorLogDao();;
    private String jiraPADCode;
    private List<String> validPADList = Arrays.asList("pad3", "pad5");
    private String boxClassesBorder;
    private String tipoDesarrollo;
    private String tipoDesarrolloFormulario;
    private final String ticketVisibleLabel = "Ticket";
    private HttpClient httpClient;
    private CookieStore cookieStore = new BasicCookieStore();

    private JiraValidationMethods validationUrlJira;
    private List<InfoJiraProject> infoJiraProjectList;

    public IDataResult<JiraResponseDTO> getValidatorByUrl(JiraValidatorByUrlRequest dto) throws Exception {
        JiraResponseDTO jiraResponseDTO = new JiraResponseDTO();
        List<JiraMessageResponseDTO> messages = new ArrayList<>();
        LOGGER.info("Fecha - hora: " + dto.getTimestamp());
        LOGGER.info("Usuario: " + dto.getUserName());
        LOGGER.info("Nombre: " + dto.getName());
        LOGGER.info("Ticket / URL: " + dto.getUrlJira());

        int successCount = 0;
        int errorCount = 0;
        int warningCount = 0;
        this.jiraApiService = new JiraApiService();
        this.httpClient = HttpClient.newHttpClient();
        this.infoJiraProjectList = InfoJiraProjectDao.getInstance().list();
        String acceptanceCriteriaGroup = "Criterio de Aceptacion";
        String prGroup="PR";
        List<String> teamBackLogTicketIdRLB = List.of("6037769"//CS
                ,"6037765"//RIC
                ,"6037763"//RIESGOS
                ,"6037905"//ENG
                ,"6037755"//FIN
        );

        var isValidUrl = validateJiraFormatURL(dto.getUrlJira());
        var issuesMetadada = getMetadataIssues(dto);
        var jsonResponse = JsonParser.parseString(issuesMetadada).getAsJsonObject();
        var jiraTicketResult = jsonResponse.getAsJsonArray("issues").get(0).getAsJsonObject();
        var prs = metadataPRs(jiraTicketResult, dto);
        jiraTicketResult.getAsJsonObject("fields").add("prs", JsonParser.parseString(prs).getAsJsonArray());

        if (issuesMetadada.isEmpty()){
            throw new HandledException("500", "no existe datos del ticket jira");
        }

        ArrayList<Map<String, Object>> result_final = new ArrayList<>();
        int ruleIdCounter = 1;

        var instancesRules = new JiraValidationMethods(dto.getUrlJira(), jiraTicketResult);
        this.infoJiraProjectList = this.infoJiraProjectList.stream().filter(obj -> obj.getTeamBackLogId() != null)
                .collect(Collectors.toList());

        // var result_29 = instancesRules.getValidatorDocumentAttachByDevType(tipoDesarrollo);
        var result_1 = instancesRules.getValidatorValidateSummaryHUTType("Validar el tipo de desarrollo en el summary", "Ticket");
        var tipoDesarrollo = result_1.get("tipoDesarrolloSummary").toString();
        var result_2 = instancesRules.getValidatorIssueType(tipoDesarrollo,"Validar que el Issue type sea Story o Dependency", "Ticket");
        var result_3 = instancesRules.getValidationFixVersion(tipoDesarrollo,"Validar que se tenga Fix Version (Solo Mallas/HOST)","Ticket");
        var result_4 = instancesRules.getValidationLabels(tipoDesarrollo,"Validar que se tengan los labels correctos", "Ticket");
        var result_5 = instancesRules.getValidationBoardProject(dto, "Validar el Tablero del proyecto", "Feature Link","Feature Link",infoJiraProjectList);
        var result_6 = instancesRules.getValidationTeamAssigned(tipoDesarrollo,true,"Validar que el equipo asignado sea el correcto", "Ticket");
        var result_7 = instancesRules.getValidationInitialTeam("Validar si se creo en el tablero de DQA", "Tablero");
        var result_8 = instancesRules.getValidationFeatureLink("Se valida el tenga un Feature Link asignado", "Feature Link");
        var result_9 = instancesRules.getValidationFeatureLinkStatus(dto, "Validar el estado Jira del Feature Link", "Feature Link");
        var result_10 = instancesRules.getValidationFeatureLinkProgramIncrement(dto, "Validar que el Feature Link tenga el Program Increment asignado y correcto (Q Actual)", "Feature Link");
        var result_11 = instancesRules.getValidationFeatureLinkRLB(dto,tipoDesarrollo,"Validar que el Feature Link tenga INC PRB o PB como label, excepto para evolutivos", "Feature Link");
        var result_12 = instancesRules.getValidationItemType("Validar Item Type sea Technical", "Ticket");
        var result_13 = instancesRules.getValidationTechStack("Validar Tech Stack sea Data - Dataproc", "Ticket");
        var result_14 = instancesRules.getValidationAcceptanceCriteria(dto,teamBackLogTicketIdRLB,tipoDesarrollo,"Validar el criterio de aceptacion, segun el tipo de desarrollo debe ser similar a la plantilla", acceptanceCriteriaGroup, infoJiraProjectList);
        var result_15 = instancesRules.getValidationValidateImpactLabel("Validar que se tengan los Impact Label correctos (Solo Mallas/HOST)","Ticket", tipoDesarrollo);
        var result_16 = instancesRules.getValidationValidateAttachment(tipoDesarrollo,"Validar la existencia de los adjuntos", "Attachment");
        var result_17 = instancesRules.getValidationDependency(teamBackLogTicketIdRLB,"Validar que exista una Dependencia asignada correctamente y comprometida (Comentario HUD Comprometida)","Dependencia");
        var result_18 = instancesRules.getValidationDependencyFeatureVsHUTFeature(teamBackLogTicketIdRLB, dto,"Validar que el ticket tenga el mismo feature link que la dependencia","Dependencia", infoJiraProjectList);
        var result_19 = instancesRules.getValidationDependencyComment(teamBackLogTicketIdRLB, dto,"Validar que la dependencia cuente con un comentario comprometido de QE o QE temporal","Dependencia", infoJiraProjectList);
        var result_20 = instancesRules.getValidationValidateSubTask(tipoDesarrollo,"Validar la existencia de las subtareas", "Subtask");
        var result_21 = instancesRules.getValidationValidateSubTaskStatus(tipoDesarrollo,"Se valida que la subtarea tenga el Status correcto", "Subtask");
        var result_22 = instancesRules.getValidationValidateSubtaskPerson(dto,tipoDesarrollo,"Validar que la subtarea tenga el VoBo de la persona en el tablero de Lideres","Subtask",infoJiraProjectList);
        var result_23 = instancesRules.getValidationValidateSubTaskValidateContractor(dto,tipoDesarrollo,"Se valida la subtarea: El email debe pertenecer a un Usuario de Negocio Interno BBVA", "Subtarea");
        var result_24 = instancesRules.getValidationAlpha(tipoDesarrollo,"Validar que la UUAA corresponda al Dominio de ALPHA", "Subtask");
        var result_25 = instancesRules.getValidationValidateJIRAStatus(tipoDesarrollo,"Validar el Status de Ticket JIRA","Ticket");
        var result_26 = instancesRules.getValidationPR(tipoDesarrollo, "Validar que se tenga una PR asociada", prGroup);
        var result_27 = instancesRules.getValidationPRBranch(tipoDesarrollo,"Validar que esté asociado a la rama correcta", prGroup);
        var result_28 = instancesRules.getValidationProductivizacionIssueLink(tipoDesarrollo, "Validar que el ticket de deployado como isChild (scaffolder)", "Ticket");
        var result_29 = instancesRules.getValidatorValidateHUTType(teamBackLogTicketIdRLB,"Detectar el tipo de Ticket Integracion", tipoDesarrollo, "Ticket");
        var result_30 = instancesRules.getValidationIFRS9("Validar los bloqueo IFRS9 en las solicitudes", "Ticket");
        var result_31 = instancesRules.getValidationURLJIRA("Validar que sea PAD3 o PAD5", "Ticket");
        var result_32 = instancesRules.getValidationFeatureLinkPAD3("Validar que el Feature Link, se recomienda que sea PAD3", "Feature Link");

        result_final.add(result_1);
        result_final.add(result_2);
        result_final.add(result_3);
        result_final.add(result_4);
        result_final.add(result_5);
        result_final.add(result_6);
        result_final.add(result_7);
        result_final.add(result_8);
        result_final.add(result_9);
        result_final.add(result_10);
        result_final.add(result_11);
        result_final.add(result_12);
        result_final.add(result_13);
        result_final.add(result_14);
        result_final.add(result_15);
        result_final.add(result_16);
        result_final.add(result_17);
        result_final.add(result_18);
        result_final.add(result_19);
        result_final.add(result_20);
        result_final.add(result_21);
        result_final.add(result_22);
        result_final.add(result_23);
        result_final.add(result_24);
        result_final.add(result_25);
        result_final.add(result_26);
        result_final.add(result_27);
        result_final.add(result_28);
        result_final.add(result_29);
        result_final.add(result_30);
        result_final.add(result_31);
        result_final.add(result_32);

        JiraValidatorLogEntity logEntity = JiraValidatorLogEntity.builder()
                .nombre(dto.getName())
                .usuario(dto.getUserName())
                .fecha(LocalDateTime.now())
                .ticket(dto.getUrlJira()).build();

        for (Map<String, Object> result : result_final) {
            JiraMessageResponseDTO message = new JiraMessageResponseDTO();
            message.setRuleId(ruleIdCounter++);
            String reglaEstado = (boolean)result.get("isValid") ? "OK" : "NOT OK";
            switch (message.getRuleId()) {
                case 1:
                    message.setRule("Validacion Summary HUT Type:");
                    message.setOrder(1);
                    logEntity.setRegla1(reglaEstado);
                    break;
                case 2:
                    message.setRule("Validacion Issue Type:");
                    message.setOrder(2);
                    logEntity.setRegla2(reglaEstado);
                    break;
                case 3:
                    message.setRule("Validacion Fix Version:");
                    message.setOrder(3);
                    logEntity.setRegla3(reglaEstado);
                    break;
                case 4:
                    message.setRule("Validacion Labels:");
                    message.setOrder(4);
                    logEntity.setRegla4(reglaEstado);
                    break;
                case 5:
                    message.setRule("Validacion Tablero Proyecto:");
                    message.setOrder(5);
                    logEntity.setRegla5(reglaEstado);
                    break;
                case 6:
                    message.setRule("Validacion Asignacion a Tablero de DQA:");
                    message.setOrder(6);
                    logEntity.setRegla6(reglaEstado);
                    break;
                case 7:
                    message.setRule("Validacion Tablero DQA:");
                    message.setOrder(7);
                    logEntity.setRegla7(reglaEstado);
                    break;
                case 8:
                    message.setRule("Validacion Feature Link:");
                    message.setOrder(8);
                    logEntity.setRegla8(reglaEstado);
                    break;
                case 9:
                    message.setRule("Validacion Feature Link Status:");
                    message.setOrder(9);
                    logEntity.setRegla9(reglaEstado);
                    break;
                case 10:
                    message.setRule("Validacion Feature Link Program Increment:");
                    message.setOrder(10);
                    logEntity.setRegla10(reglaEstado);
                    break;
                case 11:
                    message.setRule("Validacion Feature Link Incidencia/problema:");
                    message.setOrder(11);
                    logEntity.setRegla11(reglaEstado);
                    break;
                case 12:
                    message.setRule("Validacion Item Type:");
                    message.setOrder(12);
                    logEntity.setRegla12(reglaEstado);
                    break;
                case 13:
                    message.setRule("Validacion Tech Stack:");
                    message.setOrder(13);
                    logEntity.setRegla13(reglaEstado);
                    break;
                case 14:
                    message.setRule("Validacion Acceptance Criteria:");
                    message.setOrder(14);
                    logEntity.setRegla14(reglaEstado);
                    break;
                case 15:
                    message.setRule("Validacion Impact Label:");
                    message.setOrder(15);
                    logEntity.setRegla15(reglaEstado);
                    break;
                case 16:
                    message.setRule("Validacion documentos adjuntos:");
                    message.setOrder(16);
                    logEntity.setRegla16(reglaEstado);
                    break;
                case 17:
                    message.setRule("Validacion Dependencias:");
                    message.setOrder(17);
                    logEntity.setRegla17(reglaEstado);
                    break;
                case 18:
                    message.setRule("Validacion Dependencias - Feature Dependencia vs Ticket: ");
                    message.setOrder(18);
                    logEntity.setRegla18(reglaEstado);
                    break;
                case 19:
                    message.setRule("Validacion Dependencias - Comprometida por QE: ");
                    message.setOrder(19);
                    logEntity.setRegla19(reglaEstado);
                    break;
                case 20:
                    message.setRule("Validacion Subtareas:");
                    message.setOrder(20);
                    logEntity.setRegla20(reglaEstado);
                    break;
                case 21:
                    message.setRule("Validacion Subtareas Status:");
                    message.setOrder(21);
                    logEntity.setRegla21(reglaEstado);
                    break;
                case 22:
                    message.setRule("Validacion Subtareas VoBo:");
                    message.setOrder(22);
                    logEntity.setRegla22(reglaEstado);
                    break;
                case 23:
                    message.setRule("Validacion Subtareas Contractor:");
                    message.setOrder(23);
                    logEntity.setRegla23(reglaEstado);
                    break;
                case 24:
                    message.setRule("Validacion Subtarea Alpha:");
                    message.setOrder(24);
                    logEntity.setRegla24(reglaEstado);
                    break;
                case 25:
                    message.setRule("Validacion Status JIRA:");
                    message.setOrder(25);
                    logEntity.setRegla25(reglaEstado);
                    break;
                case 26:
                    message.setRule("Validacion PR:");
                    message.setOrder(26);
                    logEntity.setRegla26(reglaEstado);
                    break;
                case 27:
                    message.setRule("Validacion PR Rama Destino:");
                    message.setOrder(27);
                    logEntity.setRegla27(reglaEstado);
                    break;
                case 28:
                    message.setRule("Validacion de productivizacion:");
                    message.setOrder(28);
                    logEntity.setRegla28(reglaEstado);
                    break;
                case 29:
                    message.setRule("Validacion ticket de integracion:");
                    message.setOrder(29);
                    logEntity.setRegla29(reglaEstado);
                    break;
                case 30:
                    message.setRule("Advertencia IFRS9: Se alerta sobre la fecha de los bloqueos correspondientes a IFRS9");
                    message.setOrder(30);
                    logEntity.setRegla30(reglaEstado);
                    break;
                case 31:
                    message.setRule("Validacion Feature Link PAD3:");
                    message.setVisible(false);
                    break;
                case 32:
                    message.setRule("Validacion URL JIRA:");
                    message.setVisible(false);
                    break;
                default:
                    message.setRule("Regla desconocida");
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

        jiraResponseDTO.setData(messages.stream().filter(JiraMessageResponseDTO::getVisible)
                .sorted(Comparator.comparing(JiraMessageResponseDTO::getOrder))
                .collect(Collectors.toList()));
        jiraResponseDTO.setSuccessCount(successCount);
        jiraResponseDTO.setErrorCount(errorCount);
        jiraResponseDTO.setWarningCount(warningCount);

        try {
            jiraValidatorLogDao.insertJiraValidatorLog(logEntity);
            System.out.println("Datos insertados correctamente en la base de datos.");
        } catch (Exception e) {
            System.err.println("Error general: " + e.getMessage());
        }

        return new SuccessDataResult<>(jiraResponseDTO, "Reglas de validacion");
    }

    public String getMetadataIssues(JiraValidatorByUrlRequest dto) throws Exception {
        var tickets = List.of(dto.getUrlJira());
        var query = "key%20in%20(" + String.join(",", tickets) + ")";

        var url = ApiJiraName.URL_API_JIRA_SQL + query + this.jiraApiService.getQuerySuffixURL();
        return this.jiraApiService.GetJiraAsync(dto.getUserName(), dto.getToken() ,url);
    }

    public String metadataPRs(JsonObject jiraTicketResult, JiraValidatorByUrlRequest dto) throws Exception {
        String idTicket = jiraTicketResult.getAsJsonObject().get("id").getAsString();
        var url = ApiJiraName.URL_API_JIRA_PULL_REQUEST + idTicket + "&applicationType=stash&dataType=pullrequest";
        List<Map<String,Object>> prs = new ArrayList<>();
        String resultPRs = this.jiraApiService.GetJiraAsync(dto.getUserName(), dto.getToken(), url);
        
        var prsJsonResponse = JsonParser.parseString(resultPRs).getAsJsonObject();
        if(!prsJsonResponse
                .getAsJsonArray("detail").isEmpty()){
            var detailPR = prsJsonResponse
                    .getAsJsonArray("detail")
                    .get(0).getAsJsonObject()
                    .getAsJsonArray("pullRequests");


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
        }
        Gson gson = new Gson();
        return gson.toJson(prs);
    }

    public boolean validateJiraFormatURL(String jiraURL) {
        String regexPattern = "^(?:https://jira.globaldevtools.bbva.com/(?:browse/)?(?:plugins/servlet/mobile#issue/)?)?([a-zA-Z0-9]+-[a-zA-Z0-9]+)$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(jiraURL.toLowerCase());
        return matcher.matches();
    }
}
