package com.bbva.util.ApiJiraMet;

import com.bbva.core.HandledException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.Map;

public class ValidationUrlJira {
    private String jiraCode;
    private String validPADList;
    private String boxClassesBorder;
    private String username;
    private String token;
    private String url;

    public ValidationUrlJira(String jiraCode, String validPADList, String boxClassesBorder, String username, String token, String url) {
        this.jiraCode = jiraCode;
        this.validPADList = validPADList;
        this.boxClassesBorder = boxClassesBorder;
        this.username = username;
        this.token = token;
        this.url = url;
    }

    private String GetJiraAsync(String username, String token,String url)
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

}
