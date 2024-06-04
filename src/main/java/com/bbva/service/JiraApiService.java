
package com.bbva.service;

import com.bbva.core.HandledException;
import com.bbva.dto.issueticket.request.authJiraDtoRequest;
import com.bbva.util.ApiJiraName;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

import java.net.http.HttpClient;
import java.util.*;
import java.util.logging.Logger;

public class JiraApiService {
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
    private HttpClient httpClient;
    private CookieStore cookieStore = new BasicCookieStore();
    Map<String, String> customFields = new HashMap<>();

    public JiraApiService(){

    }
    public String getQuerySuffixURL() {
        int maxResults = 500;
        boolean jsonResult = true;
        String expand = "changelog";
        String fields = String.join(",", getTicketsByIdFieldsToGet());

        return String.format("&maxResults=%d&json_result=%b&expand=%s&fields=%s", maxResults, jsonResult, expand,
                fields);
    }
    private List<String> getTicketsByIdFieldsToGet() {
        List<String> fieldsToGet = new ArrayList<>();
        // Agregar campos predeterminados
        fieldsToGet.add("key");
        fieldsToGet.add("summary");
        fieldsToGet.add("comment");
        fieldsToGet.add("assignee");
        fieldsToGet.add("labels");
        fieldsToGet.add("project");
        fieldsToGet.add("updated");
        fieldsToGet.add("due");
        fieldsToGet.add("status");
        fieldsToGet.add("subtasks");
        fieldsToGet.add("description");
        fieldsToGet.add("created");
        fieldsToGet.add("issuetype");
        fieldsToGet.add("issuelinks");
        fieldsToGet.add("attachment");

        // Agregar campos personalizados
        fieldsToGet.addAll(customFields.values());

        return fieldsToGet;
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
    public void getBasicSession(String username, String password, CloseableHttpClient httpclient) throws Exception
    {
        var authJira =  new authJiraDtoRequest(username,password);
        var gson = new GsonBuilder().setPrettyPrinting().create();

        HttpPost httpPost = new HttpPost(ApiJiraName.URL_API_JIRA_SESSION);
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
    public String GetJiraAsync(String username, String token,String url)
            throws Exception
    {
        Object responseBody = null;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //String url = URL_API_JIRA + issueTicketCode;
        HttpGet httpGet = new HttpGet(url);
        //StringEntity requestEntity = new StringEntity(jsonString, "UTF-8");
        //httpPut.setEntity(requestEntity);
        httpGet.setHeader("Content-Type", "application/json");

        Integer responseCode =0;
        String responseBodyString = "";
        HttpEntity entity = null;
        try(CloseableHttpClient httpclient = HttpClients.createDefault()){
            getBasicSession(username, token, httpclient);
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
            throw new HandledException(responseCode.toString(), "Error al actualizar tickets, revise los datos ingresados");
        }

        return responseBodyString;
    }
}

