package com.bbva.service;

import com.bbva.core.HandledException;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.IssueTicketDao;
import com.bbva.dto.issueticket.request.WorkOrderDtoRequest;
import com.bbva.dto.issueticket.response.issueTicketDtoResponse;
import com.bbva.dto.issueticket.request.sourceTicketDtoRequest;
import com.bbva.dto.issueticket.response.sourceTicketDtoResponse;
import com.bbva.dto.jira.request.IssueBulkDto;
import com.bbva.dto.jira.request.IssueDto;
import com.bbva.dto.jira.response.IssueBulkResponse;
import com.bbva.entities.issueticket.WorkOrder;
import com.bbva.entities.issueticket.WorkOrderDetail;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mysql.cj.util.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class IssueTicketService {
    private final IssueTicketDao issueTicketDao = new IssueTicketDao();
    private static final String URL_API_JIRA = "https://globaldevtools.bbva.com/jira/rest/api/2/issue/";
    private static final String URL_API_JIRA_BULK = "https://globaldevtools.bbva.com/jira/rest/api/2/issue/bulk";
    private static final String HEADER_COOKIE_JIRA = "_oauth2_proxy=";
    private static HttpClient httpClient;
    private static final AtomicReference<HttpClient> httpINSTANCE = new AtomicReference<>();

    public static synchronized HttpClient getHttpClientInstance() {
        if (httpClient == null) {
            httpClient = HttpClient.newBuilder().build();
        }
        return httpClient;
    }

    public static HttpClient getHttpInstance() {
        HttpClient instance = httpINSTANCE.get();
        if (instance == null) {
            synchronized (IssueTicketService.class) {
                instance = httpINSTANCE.get();
                if (instance == null) {
                    instance = HttpClient.newBuilder().build();
                    httpINSTANCE.set(instance);
                }
            }
        }
        return instance;
    }

    /* rebase singleton
    private static Gson gson;

    private static final AtomicReference<IssueTicketDao> INSTANCE = new AtomicReference<>();
    public static IssueTicketDao getInstance() {
        INSTANCE.compareAndSet(null, new IssueTicketDao());
        return INSTANCE.get();
    }

    public static synchronized Gson getGsonInstance() {
        if (gson == null) {
            gson = new GsonBuilder().setPrettyPrinting().create();
        }
        return gson;
    }

     */

    public boolean expiredTokenValidate(long time) {
        Date dateNowUTC = Date.from(Instant.now());
        Date dateTokenUTC = Date.from(Instant.ofEpochMilli(time*1000L));
        return dateNowUTC.after(dateTokenUTC);
    }

    public IDataResult insert(WorkOrderDtoRequest dto)
            throws Exception {
        try {
            if(dto.workOrderDetail==null || dto.workOrderDetail.stream().count() == 0){
                return new ErrorDataResult(null,"500","Para poder registrar debe seleccionar al menos una plantilla");
            }
            var workOrderRequest = new WorkOrder(0, dto.workOrderCode, dto.folio, dto.boardId, dto.projectId, dto.sourceId, dto.sourceName
                    , dto.flowType, 1, 1, dto.registerUserId, new Date(), null, 0);

            var countWorkOrder= issueTicketDao.findRecordWorkOrder(workOrderRequest);
            if (countWorkOrder>0) {
                return new ErrorDataResult(null,"500","Existe un registro con los mismos datos (proyecto, proceso, folio, id fuente, fuente)");
            }
            var workOrderDetailsRequest = dto.workOrderDetail.stream()
                    .map(s -> new WorkOrderDetail(0, 0, s.templateId, "", "ready", dto.registerUserId, new Date(), null))
                    .collect(Collectors.toList());

            var issuesRequests = issueTicketDao.getDataRequestIssueJira2(workOrderRequest, workOrderDetailsRequest);

            try{
                createTicketJira2(dto.token, issuesRequests, workOrderDetailsRequest);
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


    public IDataResult update(WorkOrderDtoRequest dto)
            throws Exception {
        try {

            WorkOrder workOrderRequest = new WorkOrder(dto.workOrderId, dto.workOrderCode, dto.folio, dto.boardId, dto.projectId, dto.sourceId, dto.sourceName
                    , dto.flowType, 1, 1, dto.registerUserId, new Date(), null, 0);

            if (dto.workOrderId == 0) {
                return new ErrorDataResult(null,"500","Es necesario el código de registro (workOrderId) para la edición");
            }

            var workOrder = issueTicketDao.ListWorkOrder(dto.workOrderId).stream()
                    .findFirst().orElse(null);
            workOrder.board_id = workOrderRequest.board_id;
            workOrder.feature = workOrderRequest.feature;
            workOrder.folio = workOrderRequest.folio;
            workOrder.source_id = workOrderRequest.source_id;
            workOrder.source_name = workOrderRequest.source_name;

            var workOrderDetails = issueTicketDao.ListWorkOrderDetails(workOrder.work_order_id);
            if (workOrderDetails != null && workOrderDetails.stream().count()>0) {
                workOrderDetails = workOrderDetails.stream().filter(w -> !StringUtils.isNullOrEmpty(w.issue_code)).collect(Collectors.toList());
                updateTicketJira(dto.token, workOrder, workOrderDetails);
                issueTicketDao.UpdateWorkOrder(workOrder);
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
            WorkOrder workOrderRequest = new WorkOrder(dto.workOrderId, dto.workOrderCode, dto.folio, dto.boardId, dto.projectId, dto.sourceId, dto.sourceName
                    , dto.flowType, 1, 1, dto.registerUserId, new Date(), null, 0);

            workOrderDetailsRequest = dto.workOrderDetail.stream().map(s -> {
                var workorderid = dto.workOrderId > 0 ? dto.workOrderId : workOrderRequest.work_order_id;
                return new WorkOrderDetail(0, workorderid, s.templateId, "", "ready", dto.registerUserId, new Date(), null);
            }).collect(Collectors.toList());
            var issuesRequests = issueTicketDao.getDataRequestIssueJira2(workOrderRequest, workOrderDetailsRequest);
            createTicketJira2(dto.token, issuesRequests, workOrderDetailsRequest);
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

    private void createTicketJira(String cookie_oauth, WorkOrder workOrder, List<WorkOrderDetail> workOrderDetail)
            throws Exception
    {
        var httpClient = HttpClient.newHttpClient();
        Map<Integer, IssueDto> issuesRequests = issueTicketDao.getDataRequestIssueJira(workOrder, workOrderDetail);
        for(Map.Entry<Integer, IssueDto> issue : issuesRequests.entrySet())
        {
            PostResponseAsync(cookie_oauth, issue.getKey(), issue.getValue(), workOrderDetail);
        }
    }

    private void createTicketJira2(String cookie_oauth, IssueBulkDto issuesRequests, List<WorkOrderDetail> workOrderDetail)
            throws Exception
    {
        var issuesGenerates = PostResponseAsync2(cookie_oauth, issuesRequests);
        for (int i = 0; i < issuesGenerates.issues.size() && i < workOrderDetail.size(); i++) {
            workOrderDetail.get(i).setIssue_code(issuesGenerates.issues.get(i).getKey());
        }
    }

    private void updateTicketJira(String cookie_oauth, WorkOrder workOrder, List<WorkOrderDetail> workOrderDetail)
            throws Exception
    {
        Map<String, IssueDto> issuesRequests = issueTicketDao.getDataRequestIssueJiraEdit(workOrder, workOrderDetail);
        for (Map.Entry<String, IssueDto> issue : issuesRequests.entrySet())
        {
            PutResponseEditAsync(cookie_oauth, issue.getKey(), issue.getValue());
        }
    }

    private Object PostResponseAsync(String cookie_oauth, int templaye_id, IssueDto issueJira, List<WorkOrderDetail> workOrderDetail)
            throws Exception
    {
        Object responseBody = null;
        var gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(issueJira);
        String cookie = HEADER_COOKIE_JIRA + cookie_oauth;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_API_JIRA))
                .header("Content-type", "application/json")
                .headers("Cookie", cookie)
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        var httpClient = HttpClient.newBuilder().build();
        CompletableFuture<HttpResponse<String>> futureResponse = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = futureResponse.get();
        Integer responseCode = response.statusCode();

        if (responseCode.equals(302)) {
            throw new HandledException(responseCode.toString(), "Token Expirado");
        }
        if (responseCode>=400 && responseCode<=500) {
            throw new HandledException(responseCode.toString(), "Error al intentar generar tickets, revise los datos ingresados");
        }
        responseBody = response.body();
        var responseBodyString = responseBody.toString();
        IssueDto issueCreated = new Gson().fromJson(responseBodyString, IssueDto.class);
        for (WorkOrderDetail item:workOrderDetail)
        {
            if (item.template_id.equals(templaye_id))
                item.issue_code = issueCreated.key;
        }

        return responseBody;
    }

    private IssueBulkResponse PostResponseAsync2(String cookie_oauth, IssueBulkDto issueJira)
            throws Exception
    {
        // NOTA: el api bulk de jira permite hasta 50 issues por petición
        var gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(issueJira);
        String cookie = HEADER_COOKIE_JIRA + cookie_oauth;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_API_JIRA_BULK))
                .header("Content-type", "application/json")
                .headers("Cookie", cookie)
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        var futureResponse = getHttpInstance().sendAsync(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = futureResponse.get();
        Integer responseCode = response.statusCode();

        if (responseCode.equals(302)) {
            throw new HandledException(responseCode.toString(), "Token Expirado");
        }
        if (responseCode>=400 && responseCode<=500) {
            throw new HandledException(responseCode.toString(), "Error al intentar generar tickets, revise los datos ingresados");
        }
        var responseBodyString = response.body();

        var issueCreated = gson.fromJson(responseBodyString, IssueBulkResponse.class);

        return issueCreated;
    }

    private Object PutResponseEditAsync(String cookie_oauth,String issueTicketCode, IssueDto issueJira)
            throws Exception
    {
        Object responseBody = null;
        var gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(issueJira);

        String url = URL_API_JIRA + issueTicketCode;
        String cookie = HEADER_COOKIE_JIRA + cookie_oauth;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-type", "application/json")
                .headers("Cookie", cookie)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        CompletableFuture<HttpResponse<String>> futureResponse = getHttpInstance().sendAsync(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = futureResponse.get();
        Integer responseCode = response.statusCode();
        if (responseCode.equals(302)) {
            throw new HandledException(responseCode.toString(), "Token Expirado");
        }
        if (responseCode>=400 && responseCode<=500) {
            throw new HandledException(responseCode.toString(), "Error al actualizar tickets, revise los datos ingresados");
        }
        responseBody = response.body();

        return responseBody;
    }
}
