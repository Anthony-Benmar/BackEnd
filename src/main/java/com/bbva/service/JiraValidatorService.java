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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.bbva.common.jiraValidador.JiraValidatorConstantes.*;

public class JiraValidatorService {
    private static final Logger LOGGER = Logger.getLogger(JiraValidatorService.class.getName());
    private final JiraApiService jiraApiService;
    private final JiraValidatorLogDao jiraValidatorLogDao;
    private final InfoJiraProjectDao infoJiraProjectDao;
    private static final String GROUP_TICKET = "Ticket";
    private static final String GROUP_FEATURE_LINK = "Feature Link";
    private static final String GROUP_PR = "PR";
    private static final String GROUP_DEPENDENCY = "Dependencia";
    private static final String GROUP_SUBTASK = "Subtask";
    private static final String GROUP_ACCEPTANCE_CRITERIA = "Criterio de Aceptacion";
    private static final String TEAM_BACKLOG_DQA_ID = "2461905";
    private static final List<String> TEAM_BACKLOG_RLB_ID = List.of("6037769"//CS
            ,"6037765"//RIC
            ,"6037763"//RIESGOS
            ,"6037905"//ENG
            ,"6037755"//FIN
    );

    public JiraValidatorService(JiraApiService jiraApiService, JiraValidatorLogDao jiraValidatorLogDao, InfoJiraProjectDao infoJiraProjectDao) {
        this.jiraApiService = jiraApiService;
        this.jiraValidatorLogDao = jiraValidatorLogDao;
        this.infoJiraProjectDao = infoJiraProjectDao;
    }

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
        List<InfoJiraProject> infoJiraProjectList = infoJiraProjectDao.list();
        String currentQ = infoJiraProjectDao.currentQ();

        JsonObject issueMetadataJsonObject = getMetadataIssues(dto, List.of(dto.getUrlJira()));
        if (issueMetadataJsonObject.isEmpty()){
            throw new HandledException("500", "no existe datos del ticket jira");
        }
        JsonObject jiraTicketResult = issueMetadataJsonObject.getAsJsonArray(ISSUES).get(0).getAsJsonObject();
        
        String featureLink = getFeatureLink(jiraTicketResult);
        JsonObject featureLinkMetadataJsonObject = getMetadataIssues(dto, List.of(featureLink));

        String prs = metadataPRs(jiraTicketResult, dto);
        jiraTicketResult.getAsJsonObject(FIELDS).add("prs", JsonParser.parseString(prs).getAsJsonArray());

        JsonArray subTasks = jiraTicketResult
                .getAsJsonObject(FIELDS)
                .getAsJsonArray(SUBTASKS);
        Map<String, JsonObject> subtaskMetadataMap = buildSubtaskMetadataMap(dto, subTasks);

        ArrayList<Map<String, Object>> resultFinal = new ArrayList<>();
        int ruleIdCounter = 1;

        var instancesRules = new JiraValidationMethods(dto.getUrlJira(), jiraTicketResult,featureLink,featureLinkMetadataJsonObject,currentQ);
        if (!infoJiraProjectList.isEmpty()) {
            infoJiraProjectList = infoJiraProjectList.stream().filter(obj -> obj.getTeamBackLogId() != null)
                    .collect(Collectors.toList());
        }

        var result1 = instancesRules.getValidatorValidateSummaryHUTType("Validar el tipo de desarrollo en el summary", GROUP_TICKET);
        var tipoDesarrollo = result1.get("tipoDesarrolloSummary").toString();
        String teamBacklogId = getTeamBackLogId(tipoDesarrollo,jiraTicketResult);
        var result2 = instancesRules.getValidatorIssueType(tipoDesarrollo,"Validar que el Issue type sea Story o Dependency", GROUP_TICKET);
        var result3 = instancesRules.getValidationFixVersion(tipoDesarrollo,"Validar que se tenga Fix Version (Solo Mallas/HOST)",GROUP_TICKET);
        var result4 = instancesRules.getValidationLabels(tipoDesarrollo,"Validar que se tengan los labels correctos", GROUP_TICKET);
        var result5 = instancesRules.getValidationBoardProject(teamBacklogId, "Validar el Tablero del proyecto", GROUP_FEATURE_LINK, infoJiraProjectList);
        var result6 = instancesRules.getValidationTeamAssigned(tipoDesarrollo,true,"Validar que el equipo asignado sea el correcto", GROUP_TICKET);
        var result7 = instancesRules.getValidationInitialTeam("Validar si se creo en el tablero de DQA", "Tablero");
        var result8 = instancesRules.getValidationFeatureLink("Se valida el tenga un Feature Link asignado", GROUP_FEATURE_LINK);
        var result9 = instancesRules.getValidationFeatureLinkStatus( "Validar el estado Jira del Feature Link", GROUP_FEATURE_LINK);
        var result10 = instancesRules.getValidationFeatureLinkProgramIncrement("Validar que el Feature Link tenga el Program Increment asignado y correcto (Q Actual)", GROUP_FEATURE_LINK);
        var result11 = instancesRules.getValidationFeatureLinkRLB(teamBacklogId, TEAM_BACKLOG_RLB_ID,"Validar que el Feature Link tenga INC PRB o PB como label, excepto para evolutivos", GROUP_FEATURE_LINK);
        var result12 = instancesRules.getValidationItemType("Validar Item Type sea Technical", GROUP_TICKET);
        var result13 = instancesRules.getValidationTechStack("Validar Tech Stack sea Data - Dataproc", GROUP_TICKET);
        var result14 = instancesRules.getValidationAcceptanceCriteria(TEAM_BACKLOG_RLB_ID,tipoDesarrollo,"Validar el criterio de aceptacion, segun el tipo de desarrollo debe ser similar a la plantilla", GROUP_ACCEPTANCE_CRITERIA);
        var result15 = instancesRules.getValidationValidateImpactLabel("Validar que se tengan los Impact Label correctos (Solo Mallas/HOST)",GROUP_TICKET, tipoDesarrollo);
        var result16 = instancesRules.getValidationValidateAttachment(tipoDesarrollo,"Validar la existencia de los adjuntos", "Attachment");
        var result17 = instancesRules.getValidationDependency(teamBacklogId, TEAM_BACKLOG_RLB_ID,"Validar que exista una Dependencia asignada correctamente y comprometida (Comentario HUD Comprometida)",GROUP_DEPENDENCY);
        var result18 = instancesRules.getValidationDependencyFeatureVsHUTFeature(teamBacklogId, TEAM_BACKLOG_RLB_ID, dto,"Validar que el ticket tenga el mismo feature link que la dependencia",GROUP_DEPENDENCY);
        var result19 = instancesRules.getValidationDependencyComment(teamBacklogId, TEAM_BACKLOG_RLB_ID, dto,"Validar que la dependencia cuente con un comentario comprometido de QE o QE temporal",GROUP_DEPENDENCY, infoJiraProjectList);
        var result20 = instancesRules.getValidationValidateSubTask(tipoDesarrollo,"Validar la existencia de las subtareas", GROUP_SUBTASK);
        var result21 = instancesRules.getValidationValidateSubTaskStatus(tipoDesarrollo,"Se valida que la subtarea tenga el Status correcto", GROUP_SUBTASK);
        var result22 = instancesRules.getValidationValidateSubtaskPerson(subtaskMetadataMap, teamBacklogId,"Validar que la subtarea tenga el VoBo de la persona en el tablero de Lideres",GROUP_SUBTASK, infoJiraProjectList);
        var result23 = instancesRules.getValidationValidateSubTaskValidateContractor(subtaskMetadataMap,"Se valida la subtarea: El email debe pertenecer a un Usuario de Negocio Interno BBVA", "Subtarea");
        var result24 = instancesRules.getValidationAlpha(tipoDesarrollo,"Validar que la UUAA corresponda al Dominio de ALPHA", GROUP_SUBTASK);
        var result25 = instancesRules.getValidationValidateJIRAStatus(tipoDesarrollo,"Validar el Status de Ticket JIRA",GROUP_TICKET);
        var result26 = instancesRules.getValidationPR(tipoDesarrollo, "Validar que se tenga una PR asociada", GROUP_PR);
        var result27 = instancesRules.getValidationPRBranch("Validar que esté asociado a la rama correcta", GROUP_PR);
        var result28 = instancesRules.getValidationProductivizacionIssueLink(tipoDesarrollo, "Validar que el ticket de deployado como isChild (scaffolder)", GROUP_TICKET);
        var result29 = instancesRules.getValidatorHUTIntegration(teamBacklogId,TEAM_BACKLOG_RLB_ID,"Detectar el tipo de Ticket Integracion", tipoDesarrollo, GROUP_TICKET);
        var result30 = instancesRules.getValidationIFRS9("Validar los bloqueo IFRS9 en las solicitudes", GROUP_TICKET);

        resultFinal.add(result1);
        resultFinal.add(result2);
        resultFinal.add(result3);
        resultFinal.add(result4);
        resultFinal.add(result5);
        resultFinal.add(result6);
        resultFinal.add(result7);
        resultFinal.add(result8);
        resultFinal.add(result9);
        resultFinal.add(result10);
        resultFinal.add(result11);
        resultFinal.add(result12);
        resultFinal.add(result13);
        resultFinal.add(result14);
        resultFinal.add(result15);
        resultFinal.add(result16);
        resultFinal.add(result17);
        resultFinal.add(result18);
        resultFinal.add(result19);
        resultFinal.add(result20);
        resultFinal.add(result21);
        resultFinal.add(result22);
        resultFinal.add(result23);
        resultFinal.add(result24);
        resultFinal.add(result25);
        resultFinal.add(result26);
        resultFinal.add(result27);
        resultFinal.add(result28);
        resultFinal.add(result29);
        resultFinal.add(result30);

        JiraValidatorLogEntity logEntity = JiraValidatorLogEntity.builder()
                .nombre(dto.getName())
                .usuario(dto.getUserName())
                .fecha(LocalDateTime.now())
                .ticket(dto.getUrlJira()).build();

        for (Map<String, Object> result : resultFinal) {
            JiraMessageResponseDTO message = new JiraMessageResponseDTO();
            message.setRuleId(ruleIdCounter++);
            RuleConfig config = reglasConfig.getOrDefault(message.getRuleId(), new RuleConfig("Regla desconocida", 0));
            message.setOrder(config.getOrder());
            message.setRule(config.getRuleTitle());
            String ruleStatus = (boolean)result.get("isValid") ? "OK" : "NOT OK";
            actualizarLogEntity(logEntity, message.getRuleId(), ruleStatus);

            message.setVisible(config.isVisible());
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
            LOGGER.info("Datos JiraValidatorLog insertados correctamente.");
        } catch (Exception e) {
            LOGGER.info("Error JiraValidatorLog insercion BD: " + e.getMessage());
        }

        return new SuccessDataResult<>(jiraResponseDTO, "Reglas de validacion");
    }

    private String getFeatureLink(JsonObject jiraTicketResult) {
        if (jiraTicketResult == null || !jiraTicketResult.has(FIELDS)) {
            return "";
        }

        JsonObject fields = jiraTicketResult.getAsJsonObject(FIELDS);
        if (fields == null || !fields.has(CUSTOMFIELD_10004)) {
            return "";
        }

        JsonElement featureLinkElement = fields.get(CUSTOMFIELD_10004);
        if (featureLinkElement != null && !featureLinkElement.isJsonNull()) {
            return featureLinkElement.getAsString();
        }

        return "";
    }


    public String metadataPRs(JsonObject jiraTicketResult, JiraValidatorByUrlRequest dto) throws Exception {
        String idTicket = jiraTicketResult.getAsJsonObject().get("id").getAsString();
        String url = ApiJiraName.URL_API_JIRA_PULL_REQUEST + idTicket + "&applicationType=stash&dataType=pullrequest";
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
        }
        Gson gson = new Gson();
        return gson.toJson(prs);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class RuleConfig {
        private final Integer order;
        private final String ruleTitle;
        private final boolean visible;

        public RuleConfig(String ruleTitle, Integer order) {
            this(order, ruleTitle, true);
        }
    }

    private static final Map<Integer, RuleConfig> reglasConfig =  Map.ofEntries(
            Map.entry( 1, new RuleConfig("Validacion Summary HUT Type:", 1)),
            Map.entry(2, new RuleConfig("Validacion Issue Type:", 2)),
            Map.entry(3, new RuleConfig("Validacion Fix Version:", 3)),
            Map.entry(4, new RuleConfig("Validacion Labels:", 4)),
            Map.entry( 5, new RuleConfig("Validacion Tablero Proyecto:", 5)),
            Map.entry(6, new RuleConfig("Validacion Asignacion a Tablero de DQA:", 6)),
            Map.entry(7, new RuleConfig("Validacion Tablero DQA:", 7)),
            Map.entry(8, new RuleConfig("Validacion Feature Link:", 8)),
            Map.entry(9, new RuleConfig("Validacion Feature Link Status:", 9)),
            Map.entry(10, new RuleConfig("Validacion Feature Link Program Increment:", 10)),
            Map.entry(11, new RuleConfig("Validacion Feature Link Incidencia/problema:", 11)),
            Map.entry(12, new RuleConfig("Validacion Item Type:", 12)),
            Map.entry(13, new RuleConfig("Validacion Tech Stack:", 13)),
            Map.entry(14, new RuleConfig("Validacion Acceptance Criteria:", 14)),
            Map.entry(15, new RuleConfig("Validacion Impact Label:", 15)),
            Map.entry(16, new RuleConfig("Validacion documentos adjuntos:", 16)),
            Map.entry(17, new RuleConfig("Validacion Dependencias:", 17)),
            Map.entry(18, new RuleConfig("Validacion Dependencias - Feature Dependencia vs Ticket:", 18)),
            Map.entry(19, new RuleConfig("Validacion Dependencias - Comprometida por QE:", 19)),
            Map.entry(20, new RuleConfig("Validacion Subtareas:", 20)),
            Map.entry(21, new RuleConfig("Validacion Subtareas Status:", 21)),
            Map.entry(22, new RuleConfig("Validacion Subtareas VoBo:", 22)),
            Map.entry(23, new RuleConfig("Validacion Subtareas Contractor:", 23)),
            Map.entry(24, new RuleConfig("Validacion Subtarea Alpha:", 24)),
            Map.entry(25, new RuleConfig("Validacion Status JIRA:", 25)),
            Map.entry(26, new RuleConfig("Validacion PR:", 26)),
            Map.entry(27, new RuleConfig("Validacion PR Rama Destino:", 27)),
            Map.entry(28, new RuleConfig("Validacion de productivizacion:", 28)),
            Map.entry(29, new RuleConfig("Validacion ticket de integracion:", 29)),
            Map.entry(30, new RuleConfig("Advertencia IFRS9: Se alerta sobre la fecha de los bloqueos correspondientes a IFRS9", 30))
    );

    private void actualizarLogEntity(JiraValidatorLogEntity logEntity, int ruleId, String reglaEstado) {
        try {
            logEntity.getClass()
                    .getMethod("setRegla" + ruleId, String.class)
                    .invoke(logEntity, reglaEstado);
        } catch (Exception e) {
            LOGGER.info("Error al actualizar logEntity: " + e.getMessage());
        }
    }

    public JsonObject getMetadataIssues (JiraValidatorByUrlRequest dto, List<String> jiraIssues){
        JsonObject result = new JsonObject();
        if (jiraIssues == null || jiraIssues.isEmpty()) {
            return result;
        }
        String url = buildJiraQueryUrl(jiraIssues);
        try{
            String response = jiraApiService.GetJiraAsync(dto.getUserName(), dto.getToken(), url);
            result = JsonParser.parseString(response).getAsJsonObject();
        } catch (Exception e) {
            LOGGER.info("ERROR CONSULTA JIRA LINK " + url + ": " + e.getMessage());
        }
        return result;
    }

    public String buildJiraQueryUrl(List<String> jiraIssues) {
        String query = KEY_IN + String.join(",", jiraIssues) + ")";
        return ApiJiraName.URL_API_JIRA_SQL + query + jiraApiService.getQuerySuffixURL();
    }

    public String getTeamBackLogId(String tipoDesarrollo, JsonObject jiraTicketResult) throws ParseException {
        String teamBackLogId = null;
        Date oldestDate = new SimpleDateFormat("yyyy-MM-dd").parse("9999-12-31");
        JsonArray changelog = jiraTicketResult
                .getAsJsonObject(CHANGELOG)
                .getAsJsonArray(HISTORIES);
        for (JsonElement history : changelog) {
            JsonObject historyObj = history.getAsJsonObject();
            String created = historyObj.get(CREATED).getAsString();
            Date createdDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(created);
            if (createdDate.before(oldestDate)) {
                JsonArray items = historyObj.getAsJsonArray(ITEMS);
                String field = items.get(0).getAsJsonObject().get(FIELD).getAsString();
                if (field.equals(TEAM_BACKLOG) && items.get(0).getAsJsonObject().get("to").getAsString().equals(TEAM_BACKLOG_DQA_ID)) {
                    teamBackLogId = items.get(0).getAsJsonObject().get("from").getAsString();
                    oldestDate = createdDate;
                }
            }
        }
        if (teamBackLogId == null){
            if(tipoDesarrollo.equalsIgnoreCase(MALLAS) || tipoDesarrollo.equalsIgnoreCase(HOST)) {
                teamBackLogId = jiraTicketResult.getAsJsonObject(FIELDS)
                        .getAsJsonArray(CUSTOMFIELD_13301).get(0).getAsString();
            }
            else {
                teamBackLogId = jiraTicketResult.getAsJsonObject(FIELDS)
                        .getAsJsonArray(CUSTOMFIELD_13300).get(0).getAsString();
            }
        }
        return teamBackLogId;
    }

    public Map<String, JsonObject> buildSubtaskMetadataMap(JiraValidatorByUrlRequest dto, JsonArray subTasks) {
        Map<String, JsonObject> subtaskMetadataMap = new HashMap<>();

        if (subTasks == null || subTasks.isEmpty()) {
            return subtaskMetadataMap;
        }
        for (JsonElement subTask : subTasks) {
            String subtaskKey = subTask.getAsJsonObject().get("key").getAsString();
            try {
                JsonObject metadata = getMetadataIssues(dto, List.of(subtaskKey));
                if (metadata != null && !metadata.isJsonNull()) {
                    subtaskMetadataMap.put(subtaskKey, metadata);
                }
            } catch (Exception e) {
                LOGGER.info("ERROR CONSULTA JIRA LINK PARA SUBTAREA " + subtaskKey + ": " + e.getMessage());
            }
        }
        return subtaskMetadataMap;
    }

}
