package com.bbva.service;

import com.bbva.core.HandledException;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.IssueTicketDao;
import com.bbva.dto.issueticket.request.WorkOrderDtoRequest;
import com.bbva.dto.issueticket.request.WorkOrderDtoRequest2;
import com.bbva.dto.issueticket.request.authJiraDtoRequest;
import com.bbva.dto.issueticket.response.issueTicketDtoResponse;
import com.bbva.dto.issueticket.request.sourceTicketDtoRequest;
import com.bbva.dto.issueticket.response.sourceTicketDtoResponse;
import com.bbva.dto.jira.request.IssueBulkDto;
import com.bbva.dto.jira.request.IssueDto;
import com.bbva.dto.jira.request.IssueFeatureDto;
import com.bbva.dto.jira.request.IssueUpdate;
import com.bbva.dto.jira.response.IssueBulkResponse;
import com.bbva.dto.jira.response.IssueResponse;
import com.bbva.entities.feature.JiraFeatureEntity;
import com.bbva.entities.issueticket.WorkOrder;
import com.bbva.entities.issueticket.WorkOrderDetail;
import com.bbva.util.GsonConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mysql.cj.util.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

import java.net.http.HttpClient;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class IssueTicketService {
    protected final IssueTicketDao issueTicketDao = new IssueTicketDao();
    private static final String URL_API_BASE = "https://jira.globaldevtools.bbva.com";
    private static final String URL_API_JIRA_ISSUE = "/rest/api/2/issue/";
    private static final String URL_API_JIRA = URL_API_BASE + URL_API_JIRA_ISSUE;
    private static final String URL_API_JIRA_BULK = URL_API_BASE + "/rest/api/2/issue/bulk";
    private static final String URL_API_JIRA_SESSION = URL_API_BASE + "/rest/auth/1/session";
    private static final String URL_API_JIRA_SQL = URL_API_BASE + "/rest/api/2/search?jql=";
    private static final String HEADER_COOKIE_JIRA = "_oauth2_proxy=";
    private CookieStore cookieStore = new BasicCookieStore();
    private HttpClient httpClient;

    public boolean expiredTokenValidate(long time) {
        Date dateNowUTC = Date.from(Instant.now());
        Date dateTokenUTC = Date.from(Instant.ofEpochMilli(time*1000L));
        return dateNowUTC.after(dateTokenUTC);
    }

    public void getBasicSession(String username, String password, CloseableHttpClient httpclient) throws Exception
    {
        var authJira =  new authJiraDtoRequest(username,password);
        var gson = new GsonBuilder().setPrettyPrinting().create();

        HttpPost httpPost = new HttpPost(URL_API_JIRA_SESSION);
        StringEntity requestEntity = new StringEntity(gson.toJson(authJira));
        httpPost.setEntity(requestEntity);
        httpPost.setHeader("Content-Type", "application/json");

        try(CloseableHttpResponse response = httpclient.execute(httpPost)) {
            Integer responseCode = response.getStatusLine().getStatusCode();

            if (responseCode >= 400) {
                throw new HandledException(responseCode.toString(), "Error autenticación jira");
            }
            cookieStore = new BasicCookieStore();
            Header[] headers = response.getAllHeaders();
            for (Header header : headers) {
                if (header.getName().equalsIgnoreCase("Set-Cookie")) {
                    String cookieValue = header.getValue();
                    String[] cookieParts = cookieValue.split(";")[0].split("=");
                    if (cookieParts.length == 2) {
                        String cookieName = cookieParts[0].trim();
                        String cookieValueTrimmed = cookieParts[1].trim();
                        var cookie = new BasicClientCookie(cookieName, cookieValueTrimmed);
                        cookieStore.addCookie(cookie);
                    }
                }
            }
        }
    }

    public IDataResult insert(WorkOrderDtoRequest dto)
            throws Exception {
        try {
            if(dto.workOrderDetail==null || dto.workOrderDetail.stream().count() == 0){
                return new ErrorDataResult(null,"500","Para poder registrar debe seleccionar al menos una plantilla");
            }
            var workOrderRequest = new WorkOrder(0, dto.feature, dto.folio, dto.boardId, dto.projectId, dto.sourceId, dto.sourceName
                    , dto.flowType, 1, 1, dto.registerUserId, new Date(), null, 0);

            var countWorkOrder= issueTicketDao.findRecordWorkOrder(workOrderRequest);
            if (countWorkOrder>0) {
                return new ErrorDataResult(null,"500","Existe un registro con los mismos datos (proyecto, proceso, folio, id fuente, fuente)");
            }
            var workOrderDetailsRequest = dto.workOrderDetail.stream()
                    .map(s -> new WorkOrderDetail(0, 0, s.templateId, "", "ready", dto.registerUserId, new Date(), null))
                    .collect(Collectors.toList());
            var objFeature = new JiraFeatureEntity(0, dto.feature,"","","",dto.jiraProjectId, dto.jiraProjectName);
            var issuesRequests = issueTicketDao.getDataRequestIssueJira2(workOrderRequest, workOrderDetailsRequest, objFeature);

            try{
                createTicketJira2(dto, issuesRequests, workOrderDetailsRequest);
            }catch (HandledException ex){
                return new ErrorDataResult(ex.getCause(),ex.getCode(),ex.getMessage());
            }
            workOrderDetailsRequest = workOrderDetailsRequest.stream().filter(w -> !StringUtils.isNullOrEmpty(w.issue_code)).collect(Collectors.toList());
            issueTicketDao.insertWorkOrderAndDetail(workOrderRequest, workOrderDetailsRequest);

        }catch (Exception ex){
            return new ErrorDataResult(ex.getCause(),"500","No se pudo realizar el registro");
        }
        return new SuccessDataResult(null);
    }
    public IDataResult insert2(List<WorkOrderDtoRequest2> dtoList) throws Exception {
        // Inicio insert
        List<Map<String, Object>> successFeatures = new ArrayList<>();
        List<String> failedFeatures = new ArrayList<>();

        for (WorkOrderDtoRequest2 dto : dtoList) {
            try {
                if (dto.getFeature().isEmpty() || dto.getJiraProjectName().isEmpty()){
                    failedFeatures.add(dto.feature +": No se tienen datos del Feature a crear");
                    continue;
                }
                if(dto.workOrderDetail==null || dto.workOrderDetail.isEmpty()){
                    failedFeatures.add(dto.feature + ": Sin templates seleccionados");
                    continue;
                }

                IssueResponse completedFeature = createJiraFeature(dto);

                var workOrderRequest = new WorkOrder(0, completedFeature.key, dto.folio, dto.boardId, dto.projectId,
                        dto.sourceId, dto.sourceName, dto.flowType,
                        1, 1, dto.registerUserId, new Date(), null, 0);

                var countWorkOrder = issueTicketDao.findRecordWorkOrder(workOrderRequest);
                if (countWorkOrder > 0) {
                    failedFeatures.add(dto.feature + ": Ya existe registro duplicado");
                    continue;
                }

                var workOrderDetailsRequest = dto.workOrderDetail.stream()
                        .map(s -> new WorkOrderDetail(0, 0, s.templateId, "", "ready", dto.registerUserId, new Date(), null))
                        .collect(Collectors.toList());

                var objFeature = new JiraFeatureEntity(0, completedFeature.key,"","","",dto.jiraProjectId, dto.jiraProjectName);

                var issuesRequests = issueTicketDao.getDataRequestIssueJira2(
                        workOrderRequest, workOrderDetailsRequest, objFeature);


                createTicketJira3(dto, issuesRequests, workOrderDetailsRequest);

                workOrderDetailsRequest = workOrderDetailsRequest.stream()
                        .filter(w -> !StringUtils.isNullOrEmpty(w.issue_code))
                        .collect(Collectors.toList());

                issueTicketDao.insertWorkOrderAndDetail(workOrderRequest, workOrderDetailsRequest);

                successFeatures.add(Map.of(
                        "featureName", dto.feature,
                        "featureKey", completedFeature.key,
                        "storiesCreated", workOrderDetailsRequest.size(),
                        "featureLink", "https://jira.globaldevtools.bbva.com/browse/"+completedFeature.key
                ));

            } catch (Exception ex) {
                failedFeatures.add(dto.feature + ": " + ex.getMessage());
            }
        }

        return new SuccessDataResult(Map.of(
                "success", successFeatures,
                "failed", failedFeatures
        ));
    }
    private IssueResponse createJiraFeature(WorkOrderDtoRequest2 dto) throws Exception {

        String idSdaProject = callJiraGetIdSda(dto);
        dto.setE2e(idSdaProject);

        List<String> period = new ArrayList<>();

        String originalPeriod = dto.getPeriod().get(0);
        String convertedPeriod = convertPIToQuarter(originalPeriod);
        period.add(convertedPeriod);
        dto.setPeriod(period);

        var featureRequest = issueTicketDao.getDataRequestFeatureJira(dto);

        IssueResponse jiraResponse = callJiraCreateFeatureSingle(dto, featureRequest);

        return jiraResponse;
    }

    private String convertPIToQuarter(String piFormat) {
        if (piFormat == null || !piFormat.matches("PI[1-4]-\\d{2}")) {
            throw new IllegalArgumentException("Formato inválido. Se esperaba PIX-YY (ej: PI2-25)");
        }
        int quarter = Integer.parseInt(piFormat.substring(2, 3));
        String yearSuffix = piFormat.substring(4);
        int fullYear = 2000 + Integer.parseInt(yearSuffix);
        return fullYear + "-Q" + quarter;
    }

    private String callJiraGetIdSda(WorkOrderDtoRequest2 dto) throws Exception {
        Gson gson = GsonConfig.createGson();

        String issueKey = dto.getE2e();
        String url = URL_API_JIRA + issueKey + "?fields=id";

        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Content-Type", "application/json");

        Integer responseCode = 0;
        String responseBodyString = "";
        HttpEntity entity = null;

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            getBasicSession(dto.username, dto.token, httpclient);
            httpGet.setHeader("Cookie", createCookieHeader(cookieStore.getCookies()));

            CloseableHttpResponse response = httpclient.execute(httpGet);
            responseCode = response.getStatusLine().getStatusCode();
            entity = response.getEntity();
            responseBodyString = EntityUtils.toString(entity);
            response.close();
        }

        if (responseCode.equals(302)) {
            throw new HandledException(responseCode.toString(), "Token Expirado");
        }
        if (responseCode >= 400 && responseCode <= 500) {
            throw new HandledException(responseCode.toString(), "Error al obtener Issue de Jira: " + responseBodyString);
        }

        JsonObject jsonResponse = gson.fromJson(responseBodyString, JsonObject.class);
        String issueId = jsonResponse.get("id").getAsString();

        System.out.println("stop");
        return issueId;
    }
    private IssueResponse callJiraCreateFeatureSingle(WorkOrderDtoRequest2 objAuth, IssueFeatureDto featureRequest) throws Exception {
        Gson gson = GsonConfig.createGson();
        String jsonString = gson.toJson(featureRequest);

        HttpPost httpPost = new HttpPost(URL_API_JIRA  + "?expand=fields");
        StringEntity requestEntity = new StringEntity(jsonString, "UTF-8");
        httpPost.setEntity(requestEntity);
        httpPost.setHeader("Content-Type", "application/json");

        Integer responseCode = 0;
        String responseBodyString = "";
        HttpEntity entity = null;

        try(CloseableHttpClient httpclient = HttpClients.createDefault()){
            getBasicSession(objAuth.username, objAuth.token, httpclient);
            httpPost.setHeader("Cookie", createCookieHeader(cookieStore.getCookies()));
            CloseableHttpResponse response = httpclient.execute(httpPost);
            responseCode = response.getStatusLine().getStatusCode();
            entity = response.getEntity();
            responseBodyString = EntityUtils.toString(entity);
            response.close();
        }

        if (responseCode.equals(302)) {
            throw new HandledException(responseCode.toString(), "Token Expirado");
        }
        if (responseCode >= 400 && responseCode <= 500) {
            throw new HandledException(responseCode.toString(), "Error al crear Feature en Jira: " + responseBodyString);
        }

        var issueCreated = gson.fromJson(responseBodyString, IssueResponse.class);
        return issueCreated;
    }

    public IDataResult update(WorkOrderDtoRequest dto)
            throws Exception {
        try {

            WorkOrder workOrderRequest = new WorkOrder(dto.workOrderId, dto.feature, dto.folio, dto.boardId, dto.projectId, dto.sourceId, dto.sourceName
                    , dto.flowType, 1, 1, dto.registerUserId, new Date(), null, 0);

            if (dto.workOrderId == 0) {
                return new ErrorDataResult(null,"500","Es necesario el código de registro (workOrderId) para la edición");
            }

            var issueMetadataJson = GetResponseAsync(dto.username, dto.token,URL_API_JIRA_ISSUE + dto.feature);
            var issueMetadata = JsonParser.parseString(issueMetadataJson).getAsJsonObject();
            var issueKey = issueMetadata.get("key").getAsString();


            var workOrder = issueTicketDao.ListWorkOrder(dto.workOrderId).stream()
                    .findFirst().orElse(null);
            workOrder.board_id = workOrderRequest.board_id;
            workOrder.feature = issueKey;
            workOrder.folio = workOrderRequest.folio;
            workOrder.source_id = workOrderRequest.source_id;
            workOrder.source_name = workOrderRequest.source_name;

            var workOrderDetails = issueTicketDao.ListWorkOrderDetails(workOrder.work_order_id);
            if (workOrderDetails != null && workOrderDetails.stream().count()>0) {
                workOrderDetails = workOrderDetails.stream().filter(w -> !StringUtils.isNullOrEmpty(w.issue_code)).collect(Collectors.toList());
                var ticketsUpdates = updateTicketJira(dto, workOrder, workOrderDetails);
                issueTicketDao.UpdateWorkOrder(workOrder);
                if (ticketsUpdates.stream().count()>0 &&
                        ticketsUpdates.stream().count()<workOrderDetails.stream().count()) {
                    return new SuccessDataResult(null, "Algunas HUs no pudieron actualizarse, esto puede deberse a que se encuentren en un estado no permitido para la actualización o no existan en Jira");
                }
            }
        }catch (HandledException ex){
            return new ErrorDataResult(ex.getCause(),ex.getCode(),ex.getMessage());
        }
        return new SuccessDataResult(null);
    }

    public IDataResult generate(WorkOrderDtoRequest dto)
            throws Exception {
        List<WorkOrderDetail> workOrderDetailsRequest = new ArrayList();
        try {
            WorkOrder workOrderRequest = new WorkOrder(dto.workOrderId, dto.feature, dto.folio, dto.boardId, dto.projectId, dto.sourceId, dto.sourceName
                    , dto.flowType, 1, 1, dto.registerUserId, new Date(), null, 0);

            workOrderDetailsRequest = dto.workOrderDetail.stream().map(s -> {
                var workorderid = dto.workOrderId > 0 ? dto.workOrderId : workOrderRequest.work_order_id;
                return new WorkOrderDetail(0, workorderid, s.templateId, "", "ready", dto.registerUserId, new Date(), null);
            }).collect(Collectors.toList());
            var objFeature = new JiraFeatureEntity(0, dto.feature,"","","",dto.jiraProjectId, dto.jiraProjectName);
            var issuesRequests = issueTicketDao.getDataRequestIssueJira2(workOrderRequest, workOrderDetailsRequest, objFeature);
            createTicketJira2(dto, issuesRequests, workOrderDetailsRequest);
            workOrderDetailsRequest = workOrderDetailsRequest.stream().filter(w -> !StringUtils.isNullOrEmpty(w.issue_code)).collect(Collectors.toList());
            issueTicketDao.InsertWorkOrderDetail(workOrderDetailsRequest);

        }catch (HandledException ex){
            return new ErrorDataResult(ex.getCause(),ex.getCode(),ex.getMessage());
        }
        return new SuccessDataResult(null);
    }

    public IDataResult<sourceTicketDtoResponse> listSourcesGenerated(sourceTicketDtoRequest dto)
    {
        var modelo = new sourceTicketDtoResponse();
        if (dto.projectId > 0)
        {
            modelo = issueTicketDao.listSources(dto);
        }
        return new SuccessDataResult(modelo);
    }

    public IDataResult<issueTicketDtoResponse> listIssuesGenerated(sourceTicketDtoRequest dto)
    {
        var modelo = new issueTicketDtoResponse();
        if (dto.projectId > 0)
        {
            modelo = issueTicketDao.listIssuesGenerated(dto);
        }
        return new SuccessDataResult(modelo);
    }

    private void createTicketJira2(WorkOrderDtoRequest objAuth, IssueBulkDto issuesRequests, List<WorkOrderDetail> workOrderDetail)
            throws Exception
    {
        var issuesGenerates = PostResponseAsync3(objAuth, issuesRequests);
        for (int i = 0; i < issuesGenerates.issues.size() && i < workOrderDetail.size(); i++) {
            workOrderDetail.get(i).setIssue_code(issuesGenerates.issues.get(i).getKey());
        }
    }
    private void createTicketJira3(WorkOrderDtoRequest2 objAuth, IssueBulkDto issuesRequests, List<WorkOrderDetail> workOrderDetail)
            throws Exception
    {
        //var issuesGenerates = PostResponseAsync4(objAuth, issuesRequests);
        var issuesGenerates = createIssuesInBatches(objAuth, issuesRequests);
        for (int i = 0; i < issuesGenerates.issues.size() && i < workOrderDetail.size(); i++) {
            workOrderDetail.get(i).setIssue_code(issuesGenerates.issues.get(i).getKey());
        }
    }

    private List<String> updateTicketJira(WorkOrderDtoRequest dto, WorkOrder workOrder, List<WorkOrderDetail> workOrderDetail)
            throws Exception
    {
        httpClient = HttpClient.newHttpClient();
        var objFeature = new JiraFeatureEntity(0, dto.feature,"","","", dto.jiraProjectId, dto.jiraProjectName);
        Map<String, IssueDto> issuesRequests = issueTicketDao.getDataRequestIssueJiraEdit(workOrder, workOrderDetail, objFeature);
        Integer nroFails = 0;
        Integer responseCode = 0;
        List<String> ticketsUpdates = new ArrayList();
        for (Map.Entry<String, IssueDto> issue : issuesRequests.entrySet())
        {
            responseCode = PutResponseEditAsync(dto, issue.getKey(), issue.getValue());
            if (responseCode>=400 && responseCode<=500) {
                nroFails = nroFails + 1;
            }else{
                ticketsUpdates.add(issue.getKey());
            }
        }
        if(nroFails>0 && nroFails.equals(issuesRequests.size())){
            throw new HandledException(responseCode.toString(), "No se pudo actualizar ninguna HU, esto puede deberse a un feature incorrecto o que las HU se encuentren en un estado no permitido para la actualización o no existan en Jira");
        }
        return ticketsUpdates;
    }

    private IssueBulkResponse PostResponseAsync3(WorkOrderDtoRequest objAuth, IssueBulkDto issueJira)
            throws Exception
    {
        // NOTA: el api bulk de jira permite hasta 50 issues por petición
        //var gson = new GsonBuilder().setPrettyPrinting().create();
        Gson gson = GsonConfig.createGson();
        String jsonString = gson.toJson(issueJira);

        HttpPost httpPost = new HttpPost(URL_API_JIRA_BULK);
        StringEntity requestEntity = new StringEntity(jsonString, "UTF-8");
        httpPost.setEntity(requestEntity);
        httpPost.setHeader("Content-Type", "application/json");

        Integer responseCode =0;
        String responseBodyString = "";
        HttpEntity entity = null;
        try(CloseableHttpClient httpclient = HttpClients.createDefault()){
            getBasicSession(objAuth.username, objAuth.token, httpclient);
            httpPost.setHeader("Cookie", createCookieHeader(cookieStore.getCookies()));
            CloseableHttpResponse response = httpclient.execute(httpPost);
            responseCode = response.getStatusLine().getStatusCode();
            entity = response.getEntity();
            responseBodyString = EntityUtils.toString(entity);
            response.close();
        }

        if (responseCode.equals(302)) {
            throw new HandledException(responseCode.toString(), "Token Expirado");
        }
        if (responseCode>=400 && responseCode<=500) {
            throw new HandledException(responseCode.toString(), "Error al intentar generar tickets, revise los datos ingresados");
        }
        var issueCreated = gson.fromJson(responseBodyString, IssueBulkResponse.class);
        return issueCreated;
    }

    private IssueBulkResponse PostResponseAsync4(WorkOrderDtoRequest2 objAuth, IssueBulkDto issueJira)
            throws Exception
    {
        // NOTA: el api bulk de jira permite hasta 50 issues por petición
        //var gson = new GsonBuilder().setPrettyPrinting().create();
        Gson gson = GsonConfig.createGson();
        String jsonString = gson.toJson(issueJira);

        HttpPost httpPost = new HttpPost(URL_API_JIRA_BULK);
        StringEntity requestEntity = new StringEntity(jsonString, "UTF-8");
        httpPost.setEntity(requestEntity);
        httpPost.setHeader("Content-Type", "application/json");

        Integer responseCode =0;
        String responseBodyString = "";
        HttpEntity entity = null;
        try(CloseableHttpClient httpclient = HttpClients.createDefault()){
            getBasicSession(objAuth.username, objAuth.token, httpclient);
            httpPost.setHeader("Cookie", createCookieHeader(cookieStore.getCookies()));
            CloseableHttpResponse response = httpclient.execute(httpPost);
            responseCode = response.getStatusLine().getStatusCode();
            entity = response.getEntity();
            responseBodyString = EntityUtils.toString(entity);
            response.close();
        }

        if (responseCode.equals(302)) {
            throw new HandledException(responseCode.toString(), "Token Expirado");
        }
        if (responseCode>=400 && responseCode<=500) {
            throw new HandledException(responseCode.toString(), "Error al intentar generar tickets, revise los datos ingresados");
        }
        var issueCreated = gson.fromJson(responseBodyString, IssueBulkResponse.class);
        return issueCreated;
    }

    public IssueBulkResponse createIssuesInBatches(WorkOrderDtoRequest2 objAuth, IssueBulkDto issueJira) throws Exception {
        List<IssueUpdate> allIssues = issueJira.getIssueUpdates();

        if (allIssues == null || allIssues.isEmpty()) {
            throw new IllegalArgumentException("No hay issues para crear");
        }

        List<IssueBulkResponse> responses = new ArrayList<>();
        final int BATCH_SIZE = 30;

        System.out.println("Creando " + allIssues.size() + " issues en batches de " + BATCH_SIZE);

        // Dividir en chunks de 30
        for (int i = 0; i < allIssues.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, allIssues.size());
            List<IssueUpdate> batch = allIssues.subList(i, endIndex);

            System.out.println("Procesando batch " + (i/BATCH_SIZE + 1) + " - Issues " + (i+1) + " al " + endIndex);

            // Crear DTO para este batch
            IssueBulkDto batchDto = new IssueBulkDto();
            batchDto.setIssueUpdates(batch);

            // Hacer la petición
            IssueBulkResponse batchResponse = PostResponseAsync4(objAuth, batchDto);
            responses.add(batchResponse);

            // Pausa entre requests para no saturar
            if (i + BATCH_SIZE < allIssues.size()) {
                Thread.sleep(500); // 500ms entre batches
            }
        }

        // Combinar todas las respuestas
        return combineResponses(responses);
    }

    private IssueBulkResponse combineResponses(List<IssueBulkResponse> responses) {
        IssueBulkResponse combined = new IssueBulkResponse();
        List<IssueDto> allIssues = new ArrayList<>();

        for (IssueBulkResponse response : responses) {
            if (response.issues != null) {
                allIssues.addAll(response.issues);
            }
        }

        combined.issues = allIssues;

        System.out.println("Total issues creados exitosamente: " + allIssues.size());

        return combined;
    }

    private Integer PutResponseEditAsync(WorkOrderDtoRequest objAuth,String issueTicketCode, IssueDto issueJira)
            throws Exception
    {
        Gson gson = GsonConfig.createGson();
        String jsonString = gson.toJson(issueJira);

        String url = URL_API_JIRA + issueTicketCode;
        HttpPut httpPut = new HttpPut(url);
        StringEntity requestEntity = new StringEntity(jsonString, "UTF-8");
        httpPut.setEntity(requestEntity);
        httpPut.setHeader("Content-Type", "application/json");

        Integer responseCode =0;
        String responseBodyString = "";
        HttpEntity entity = null;
        try(CloseableHttpClient httpclient = HttpClients.createDefault()){
            getBasicSession(objAuth.username, objAuth.token, httpclient);
            httpPut.setHeader("Cookie", createCookieHeader(cookieStore.getCookies()));
            CloseableHttpResponse response = httpclient.execute(httpPut);
            responseCode = response.getStatusLine().getStatusCode();
            entity = response.getEntity();
            response.close();
        }
        if (responseCode.equals(302)) {
            throw new HandledException(responseCode.toString(), "Token Expirado");
        }
        return responseCode;
    }

    private String GetResponseAsync(String username, String password, String apiPath)
            throws Exception
    {
        HttpGet httpGet = new HttpGet(URL_API_BASE + apiPath);
        httpGet.setHeader("Content-Type", "application/json");

        Integer responseCode =0;
        String responseBodyString = "";
        HttpEntity entity = null;
        try(CloseableHttpClient httpclient = HttpClients.createDefault()){
            getBasicSession(username, password, httpclient);
            httpGet.setHeader("Cookie", createCookieHeader(cookieStore.getCookies()));
            CloseableHttpResponse response = httpclient.execute(httpGet);
            responseCode = response.getStatusLine().getStatusCode();
            entity = response.getEntity();
            responseBodyString = EntityUtils.toString(entity);
            response.close();
        }

        if (responseCode.equals(302)) {
            throw new HandledException(responseCode.toString(), "Token Expirado");
        }
        if (responseCode>=400 && responseCode<=500) {
            throw new HandledException(responseCode.toString(), "El feature no existe");
        }
        return responseBodyString;
    }

    private static String createCookieHeader(List<Cookie> cookieList) {
        StringBuilder cookieHeader = new StringBuilder();
        for (Cookie responseCookie : cookieList) {
            cookieHeader.append(responseCookie.getName()).append("=").append(responseCookie.getValue()).append("; ");
        }
        if (cookieHeader.length() > 0) {
            cookieHeader.delete(cookieHeader.length() - 2, cookieHeader.length());
        }
        return cookieHeader.toString();
    }
}
