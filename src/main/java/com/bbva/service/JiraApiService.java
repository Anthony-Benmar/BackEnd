package com.bbva.service;

import com.bbva.core.HandledException;
import com.bbva.dto.issueticket.request.authJiraDtoRequest;
import com.google.gson.*;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import static com.bbva.util.ApiJiraName.URL_API_JIRA_SESSION;
import static com.bbva.util.ApiJiraName.URL_API_JIRA_SQL;

public class JiraApiService {
    private static final Logger LOGGER = Logger.getLogger(JiraApiService.class.getName());
    private final CloseableHttpClient httpClient;
    private CookieStore cookieStore = new BasicCookieStore();
    //private HttpClient httpClient;
    Map<String, String> customFields = new HashMap<>();
    private final Map<String, String> jiraHeaders = new HashMap<>();
    private final String username;
    private final String token;
    private final int maxHoursExpireCookie = 1;

    public JiraApiService(String username, String token) throws Exception {
        this.username = username;
        this.token = token;
        this.jiraHeaders.put("Content-Type", "application/json");
        this.httpClient = HttpClientBuilder.create().build();
        customFields.put("teamId", "customfield_13300");
        customFields.put("petitionerTeamId", "customfield_13301");
        customFields.put("receptorTeamId", "customfield_13302");
        customFields.put("featureLink", "customfield_10004");
        customFields.put("sprintEstimate", "customfield_10272");
        getBasicSession(this.username, this.token, this.httpClient);
    }

    //------------------------  CONNECTION JIRA ---------------------------
    public void getBasicSession(String username, String password, CloseableHttpClient httpclient)
            throws Exception {
        var authJira =  new authJiraDtoRequest(username,password);
        var gson = new GsonBuilder().setPrettyPrinting().create();

        HttpPost httpPost = new HttpPost(URL_API_JIRA_SESSION);
        StringEntity requestEntity = new StringEntity(gson.toJson(authJira));
        httpPost.setEntity(requestEntity);
        httpPost.setHeader("Content-Type", "application/json");

        try(CloseableHttpResponse response = httpclient.execute(httpPost)) {
            int responseCode = response.getStatusLine().getStatusCode();

            if (responseCode >= 400) {
                LOGGER.severe("Error autenticación jira");
                System.out.println("Error autenticación jira");
                throw new HandledException(Integer.toString(responseCode), "Error autenticación jira");
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



    private void renewJiraCookie(){
        System.out.println("Renovando cookie de jira");
        getCookiesFromToken();
    }
    private void getCookiesFromToken() {
        String cookieURLPath = URL_API_JIRA_SESSION;
        HttpPost request = new HttpPost(cookieURLPath);

        JsonObject payload = new JsonObject();
        payload.addProperty("username",  this.username);
        payload.addProperty("password", this.token);
        request.addHeader("Content-Type", "application/json");
        System.out.println("Payload: " + payload.toString());
        System.out.println("Request: " + request);

        try( CloseableHttpResponse response = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build().execute(request)) {
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode >= 400) {
                LOGGER.severe("Error al obtener cookie de jira");
                System.out.println("Error al obtener cookie de jira");
                throw new HandledException(Integer.toString(responseCode), "Error al obtener cookie de jira");
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
        } catch (Exception e) {
            LOGGER.severe("Error al obtener cookie de jira");
            System.out.println("Error al obtener cookie de jira");
            e.printStackTrace();
        }
    }


    //Este método devuelve una lista de campos que se solicitarán cuando se busquen tickets en Jira.
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




    //Este método devuelve una cadena que se agregará al final de la URL de la consulta.
    private String getQuerySuffixURL() {
        int maxResults = 500;
        boolean jsonResult = true;
        String expand = "changelog";
        String fields = String.join(",", getTicketsByIdFieldsToGet());

        return String.format("&maxResults=%d&json_result=%b&expand=%s&fields=%s", maxResults, jsonResult, expand,
                fields);
    }

    private HttpRequest.Builder getRequestBuilder(String url, String method) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers("Content-Type", "application/json")
                .method(method, HttpRequest.BodyPublishers.noBody());
    }


//    public List<Map<String, Object>> searchIssues(String jqlStr, List<String> fieldsToGet) {
//        List<Map<String, Object>> res = new ArrayList<>();
//        renewJiraCookie();
//
//        String queryURL = URL_API_JIRA_SQL + jqlStr + getQuerySuffixURL();
//        System.out.println(queryURL);
//        HttpRequest request = getRequestBuilder(queryURL, "GET").build();
//        System.out.println("REQUEST: " + request);
//
//        //HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
////
////        try {
////            HttpGet request = new HttpGet(URI.create(queryURL));
////            HttpResponse response = httpClient.execute(request);
////
////            // Verificar si la respuesta es exitosa (código de estado 200)
////            if (response.getStatusLine().getStatusCode() == 200) {
////                String responseBody = EntityUtils.toString(response.getEntity());
////
////                // Parsear la respuesta JSON
////                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
////                JsonArray issuesArray = jsonResponse.getAsJsonArray("issues");
////
////                // Iterar sobre cada objeto de problema y extraer los campos requeridos
////                for (JsonElement issueElement : issuesArray) {
////                    JsonObject issueObject = issueElement.getAsJsonObject();
////                    Map<String, Object> issueMap = new HashMap<>();
////
////                    // Agregar el campo 'key'
////                    issueMap.put("key", issueObject.get("key").getAsString());
////
////                    // Agregar los campos específicos solicitados
////                    for (String field : fieldsToGet) {
////                        if (issueObject.getAsJsonObject("fields").has(field)) {
////                            issueMap.put(field, issueObject.getAsJsonObject("fields").get(field));
////                        }
////                    }
////
////                    // Agregar el objeto de problema a la lista de resultados
////                    res.add(issueMap);
////                }
////            } else {
////                System.err.println("Error en la solicitud: " + response.getStatusLine().getStatusCode());
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//        return res;
//    }

//    public List<Map<String, Object>> searchIssues(String jqlStr, List<String> fieldsToGet) {
//        List<Map<String, Object>> res = new ArrayList<>();
//        renewJiraCookie();
//
//        String queryURL = URL_API_JIRA_SQL + jqlStr + getQuerySuffixURL();
//        System.out.println(queryURL);
//
//        try {
//            HttpGet request = new HttpGet(URI.create(queryURL));
//            HttpResponse response = httpClient.execute(request);
//
//            // Verificar si la respuesta es exitosa (código de estado 200)
//            if (response.getStatusLine().getStatusCode() == 200) {
//                String responseBody = EntityUtils.toString(response.getEntity());
//
//                // Parsear la respuesta JSON
//                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
//                JsonArray issuesArray = jsonResponse.getAsJsonArray("issues");
//
//                // Iterar sobre cada objeto de problema y extraer los campos requeridos
//                for (JsonElement issueElement : issuesArray) {
//                    JsonObject issueObject = issueElement.getAsJsonObject();
//                    Map<String, Object> issueMap = new HashMap<>();
//
//                    // Agregar el campo 'key'
//                    issueMap.put("key", issueObject.get("key").getAsString());
//
//                    // Agregar los campos específicos solicitados
//                    for (String field : fieldsToGet) {
//                        if (issueObject.getAsJsonObject("fields").has(field)) {
//                            issueMap.put(field, issueObject.getAsJsonObject("fields").get(field));
//                        }
//                    }
//
//                    // Agregar el objeto de problema a la lista de resultados
//                    res.add(issueMap);
//                }
//            } else {
//                System.err.println("Error en la solicitud: " + response.getStatusLine().getStatusCode());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return res;
//    }

    public List<Map<String, Object>> searchIssues(String jqlStr, List<String> fieldsToGet) throws Exception {
        List<Map<String, Object>> res = new ArrayList<>();
        renewJiraCookie();

        String queryURL = URL_API_JIRA_SQL + jqlStr + getQuerySuffixURL();
        System.out.println(queryURL);

        try {
            HttpGet request = new HttpGet(URI.create(queryURL));
            HttpResponse response = httpClient.execute(request);

            // Verificar si la respuesta es exitosa (código de estado 200)
            if (response.getStatusLine().getStatusCode() == 200) {
                String responseBody = EntityUtils.toString(response.getEntity());

                // Parsear la respuesta JSON
                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                JsonArray issuesArray = jsonResponse.getAsJsonArray("issues");

                // Iterar sobre cada objeto de problema y extraer los campos requeridos
                for (JsonElement issueElement : issuesArray) {
                    JsonObject issueObject = issueElement.getAsJsonObject();
                    JsonObject fieldsObject = issueObject.getAsJsonObject("fields");
                    Map<String, Object> issueMap = new HashMap<>();

                    // Agregar el campo 'key'
                    issueMap.put("key", issueObject.get("key").getAsString());

                    // Agregar los campos específicos solicitados
                    for (String field : fieldsToGet) {
                        if (fieldsObject.has(field)) {
                            issueMap.put(field, fieldsObject.get(field));
                        }
                    }

                    // Agregar el objeto de problema a la lista de resultados
                    res.add(issueMap);
                }
            } else {
                System.err.println("Error en la solicitud: " + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    public String findCommentBySentence(JsonObject commentsObject, String sentenceText) {
        String res = "";

        JsonArray comments = commentsObject.getAsJsonArray("comments");
        for (JsonElement commentElement : comments) {
            JsonObject commentObject = commentElement.getAsJsonObject();
            String bodyText = commentObject.get("body").getAsString();
            String bodyTextToSearch = bodyText.replace("*", "").toLowerCase().trim();
            if (bodyTextToSearch.contains(sentenceText.toLowerCase().trim())) {
                res = bodyText;
                break;
            }
        }

        return res;
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
    public void testConnection() {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            getBasicSession(this.username, this.token, httpclient);
            System.out.println("Connection successful.");
        } catch (Exception e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }
    }


    //--------------------- SEARCH BY TICKETS ---------------------------
    public List<Map<String, Object>> searchByTicket(List<String> tickets, List<String> fieldsToGet) throws Exception {
        String query = "key%20in%20(" + String.join(",", tickets) + ")";
        return searchIssues(query, fieldsToGet);
    }

    //--------------------- IS VALID NAME IN LIST ---------------------------

    //--------------------- GET TEAM TYPE FIELDI BY ID -----------------------

}
