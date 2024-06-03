package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.JiraValidatorByUrlResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JiraValidatorService {
    private JiraApiService jiraApiService;
    private boolean isValidURL;

    //Todas la reglas de negocio
    public IDataResult<JiraValidatorByUrlResponse> getValidatorByUrl(JiraValidatorByUrlRequest dto) { //username, token
        jiraApiService = new JiraApiService(dto.getUserName(), dto.getToken());
        jiraApiService.testConnection();

        dto.setUrlJira(dto.getUrlJira().toUpperCase());
        validateJiraURL(dto.getUrlJira());
        if (!isValidURL) {
            System.out.println("URL INVALIDA");
            return new SuccessDataResult<>(new JiraValidatorByUrlResponse("URL INVALIDA"), "URL INVALIDA");
        }else {
            System.out.println("CONEXION EXITOSA");
            return new SuccessDataResult<>(new JiraValidatorByUrlResponse("OK"), "CONEXION EXITOSA");
        }


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
                    Map<String, Object> validacionEnvioFormulario = getValidatorValidateSentToTablero05("Validar envio de formulario", ticketGroup); // validar a través de un google sheet o BD???
                    res.add(validacionEnvioFormulario);
                    Map<String, Object> validacionSummaryResult = getValidatorValidateSummaryHUTType("Validar el tipo de desarrollo en el summary", ticketGroup);
                    String tipoDesarrolloSummary = (String) validacionSummaryResult.get("tipoDesarrolloSummary");

                    Map<String, Object> validacionTipoDesarrolloResult = getValidatorValidateHUTType(
                            "Detectar el tipo de desarrollo por el prefijo de " + ticketVisibleLabel + " y el summary",
                            tipoDesarrolloSummary,
                            ticketGroup
                    );

                    currentAdjuntosToValidate = getCurrentAdjuntosByTipoDesarrollo(tipoDesarrollo);
                    res.add(validacionSummaryResult);
                    res.add(validacionTipoDesarrolloResult);
                    String teamBacklogGroup = "Tablero: " + currentTeamFieldLabel;

                    Map<String, Object> validacionTeamAssigned = getValidationTeamAssigned(
                            validacionEnvioFormulario,
                            "Validar si esta asignado en el Tablero de DQA",
                            teamBacklogGroup
                    );
                    res.add(validacionTeamAssigned);
                    Map<String, Object> issueTypeResult = getValidatorIssueType("Validar que el Issue type sea Story o Dependency", ticketGroup);
                    res.add(issueTypeResult);
                    if (!tipoDesarrollo.isEmpty()) {
                        res.add(validationURLJiraResult);
                        res.add(getValidationValidateJIRAStatus("Validar el Status de Ticket JIRA", ticketGroup));
                        Map<String, Object> validacionLabelsResult = getValidationLabels("Validar que segun el tipo de desarrollo se tengan ciertos Labels", ticketGroup);
                        List<String> tiposEspecialesDetectados = (List<String>) validacionLabelsResult.get("tiposEspecialesDetectados");
                        res.add(validacionLabelsResult);
                        if (!tiposEspecialesDetectados.isEmpty()) {
                            res.add(getValidationLabelsEspeciales("Validar tipos especiales de Labels", tiposEspecialesDetectados, ticketGroup));
                            res.add(getValidationSingleAttribute(
                                    (String) ((Map<String, Object>) jiraTicketResult).get("itemType").get("value"),
                                    "Item Type",
                                    "Technical",
                                    "Validar atributo: Item Type",
                                    ticketGroup
                            ));
                            res.add(getValidationSingleAttribute(
                                    jiraTicketResult.get("techStack") == null ? "" : (String) ((Map<String, Object>) jiraTicketResult).get("techStack").get("value"),
                                    "Tech Stack",
                                    "Data - Dataproc",
                                    "Validar Atributo: Tech Stack",
                                    ticketGroup
                            ));
                            if (isIntegrationTicket) {
                                String ticketIntegracionGroup = "Ticket de Integracion";
                                res.add(getValidationTicketIntegracion(extraTicketResults.get("parentIssueLinksDeployedTablero05"), "Detectar si es ticket de integracion", ticketIntegracionGroup));
                                res.add(getValidationTicketIntegracionVoBoPO("Detectar si requiere [C204][PO]", ticketIntegracionGroup));
                                if (tipoDesarrollo.equals("Mallas") || tipoDesarrollo.equals("HOST")) {
                                    res.add(getValidationValidateImpactLabel("Validar que se tengan los Impact Label correctos (Solo Mallas/HOST)", ticketGroup));
                                    res.add(getValidationFixVersion("Validar que se tenga Fix Version (Solo Mallas/HOST)", ticketGroup));
                                    res.add(getValidationInitialTeam("Validar si se creo en el tablero de DQA", teamBacklogGroup));

                                    getCurrentTeam();
                                    if (!currentTablename.isEmpty()) {
                                        res.add(getValidatorValidateSummaryTeam("Validar el tablero en el summary", teamBacklogGroup));
                                        getProjectDataFromLideresByLastTeam(currentTablename);
                                        if (tipoIncidenciaKey.isEmpty()) {
                                            res.add(getValidationExcelLideresTeam("Validar si el Tablero del Proyecto existe en el Excel de Lideres", teamBacklogGroup));
                                        }
                                        res.add(validacionTeamAssigned);
                                        String prGroup = "PR";

                                        Map<String, Object> prValidationResult = getValidationPR("Validar que se tenga una PR asociada", prGroup);
                                        res.add(prValidationResult);

                                        if (!urlPRActual.isEmpty()) {
                                            res.add(getValidationPRBranch("Validar que este asociado a la rama correcta", prGroup));

                                            List<Map<String, Object>> prsToValidateObject = getPrChecksToValidate();
                                            List<String> prCheckReviewers = prActual.get("reviewers").stream()
                                                    .filter(revObject -> (Boolean) revObject.get("approved"))
                                                    .map(revObject -> (String) revObject.get("user"))
                                                    .collect(Collectors.toList());
                                            List<String> prCheckReviewersOnlyUsers = prCheckReviewers.stream()
                                                    .filter(prEmail -> !prEmail.contains(".contractor") && !prEmail.contains("bot-"))
                                                    .collect(Collectors.toList());
                                            for (Map<String, Object> prCheckObject : prsToValidateObject) {
                                                res.add(getValidationPRChecks(
                                                        "Validar que la PR del " + ticketVisibleLabel + " tenga el VoBo del " + prCheckObject.get("label"),
                                                        prCheckObject,
                                                        prCheckReviewers,
                                                        prCheckReviewersOnlyUsers,
                                                        prGroup
                                                ));
                                            }
                                            if (isEnviadoFormulario) {
                                                res.add(getValidationPRvsFormulario("Validar que la PR del " + ticketVisibleLabel + " sea igual al enviado via Formulario", prGroup));
                                            }
                                            if (tipoDesarrollo.equals("Productivizacion")) {
                                                String productivizacionGroup = "Productivizacion";
                                                res.add(getValidationProductivizacionIssueLink(extraTicketResults.get("childIssueLinksDeployedTablero05Master"), "Validar tenga asociado 1 ticket deployado", productivizacionGroup));
                                                String acceptanceCriteriaGroup = "Criterio de Aceptacion";
                                                res.add(getValidationAcceptanceCriteria("Validar el criterio de aceptacion, segun el tipo de desarrollo debe ser similar a la plantilla", acceptanceCriteriaGroup));
                                                if (!mvpMallasDetectado.isEmpty() && lideresCurrentData != null) {
                                                    res.add(getValidationMVPExcelLideres("Validar el MVP detectado, buscarlo en el Excel de Lideres", acceptanceCriteriaGroup));
                                                    if ((Boolean) issueTypeResult.get("isValid")) {
                                                        setSubTasksBySummary();
                                                        String featureLinkGroup = "Feature Link";
                                                        Map<String, Object> featureLinkResult = getValidationFeatureLink("Se valida el tenga un Feature Link asignado", featureLinkGroup);
                                                        res.add(featureLinkResult);

                                                        if ((Boolean) featureLinkResult.get("isValid")) {
                                                            res.add(getValidationFeatureLinkPAD3("Validar que el Feature Link, se recomienda que sea PAD3", featureLinkGroup));
                                                            res.add(getValidationFeatureLinkStatus("Validar el estado Jira del Feature Link", featureLinkGroup));
                                                            res.add(getValidationFeatureLinkProgramIncrement("Validar que el Feature Link tenga el Program Increment asignado y correcto (Q Actual)", featureLinkGroup));
                                                        }

                                                        String dependencyGroup = "Dependencia";
                                                        Map<String, Object> dependencyResult = getValidationDependency("Validar que exista una Dependencia asignada correctamente y comprometida (Comentario HUD Comprometida)", dependencyGroup);
                                                        res.add(dependencyResult);

                                                        if ((Boolean) dependencyResult.get("isValid") && dependencyTicket != null) {
                                                            res.add(getValidationDependencyStatus("Validar el Status de la Dependencia", dependencyGroup));
                                                            if ((Boolean) featureLinkResult.get("isValid")) {
                                                                res.add(getValidationDependencyFeatureVsHUTFeature("Validar que el " + ticketVisibleLabel + " tenga el mismo Feature Link que la Dependencia", dependencyGroup));
                                                            }
                                                        }
                                                        if (!tipoDesarrollo.isEmpty()) {
                                                            if (!currentAdjuntosToValidate.isEmpty()) {
                                                                String adjuntosGroup = "Documentos Adjuntos";
                                                                for (Map<String, Object> adjuntoObject : currentAdjuntosToValidate) {
                                                                    res.add(getValidationValidateAttachment(adjuntoObject, "Se valida que el documento adjunto " + adjuntoObject.get("label") + " exista", adjuntosGroup));
                                                                }
                                                            }

                                                            List<Map<String, Object>> subtareasTipoDesarrollo = getSubtareasPorTipoDesarrollo();
                                                            for (Map<String, Object> subtarea : subtareasTipoDesarrollo) {
                                                                String subtasksGroup = "Subtarea " + subtarea.get("label");
                                                                Pair<Map<String, Object>, Map<String, Object>> subTaskResult = getValidationValidateSubTask(subtarea, "Se valida que la subtarea " + subtarea.get("label") + " exista", subtasksGroup);
                                                                res.add(subTaskResult.getFirst());
                                                                Map<String, Object> currentSubTaskDetected = subTaskResult.getSecond();
                                                                if (currentSubTaskDetected != null) {
                                                                    res.add(getValidationValidateSubTaskStatus(subtarea, currentSubTaskDetected, "Se valida que la subtarea " + subtarea.get("label") + " tenga el Status correcto", subtasksGroup));
                                                                    boolean isValidatingLideresEmail = (Boolean) currentSubTaskDetected.get("owner").get("object").get("validateEmailFromLideres");
                                                                    boolean isValidatingContractor = (Boolean) currentSubTaskDetected.get("owner").get("object").get("validateEmailContractor");
                                                                    if ((isValidatingLideresEmail || isValidatingContractor) && currentSubTaskDetected.get("item").get("status").equals("Accepted") && (tipoIncidenciaKey.isEmpty() || currentSubTaskDetected.get("owner").get("key").equals("so"))) {
                                                                        List<Map<String, Object>> acceptedSubtaskHistory = getValidateSubTaskGetAcceptedSubtask(subtarea, currentSubTaskDetected, isValidatingLideresEmail, isValidatingContractor);
                                                                        if (isValidatingLideresEmail) {
                                                                            res.add(getValidationValidateSubTaskValidateEmailExcelLideres(
                                                                                    subtarea,
                                                                                    (String) currentSubTaskDetected.get("label"),
                                                                                    (String) currentSubTaskDetected.get("owner").get("object").get("label"),
                                                                                    (String) currentSubTaskDetected.get("owner").get("key"),
                                                                                    acceptedSubtaskHistory,
                                                                                    "Se valida la subtarea " + subtarea.get("label") + " : El email debe existir en el " + excelLideresURL + ", segun el tipo de subtarea el VoBo lo debe dar el usuario correcto ",
                                                                                    subtasksGroup
                                                                            ));
                                                                        }
                                                                        if (isValidatingContractor) {
                                                                            res.add(getValidationValidateSubTaskValidateContractor(
                                                                                    subtarea,
                                                                                    (String) currentSubTaskDetected.get("label"),
                                                                                    acceptedSubtaskHistory,
                                                                                    "Se valida la subtarea " + subtarea.get("label") + " : El email debe pertenecer a un Usuario de Negocio Interno BBVA",
                                                                                    subtasksGroup
                                                                            ));
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //finally {
            //if (res.isEmpty()) {
              //  res.add(new SuccessDataResult<>("OK", "CONEXION EXITOSA"));
            //}
        //}
        ;
        return res;
    }

    public void validateJiraURL(String jiraURL) {
        String regexPattern = "^(?:https://jira.globaldevtools.bbva.com/(?:browse/)?(?:plugins/servlet/mobile#issue/)?)?([a-zA-Z0-9]+-[a-zA-Z0-9]+)$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(jiraURL.toLowerCase());
        this.isValidURL = matcher.matches();
    }

    //MEtodos de las validaciones
    //void Regla_1(){
    // /Cuerpo
    //        }
}
