package com.bbva.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class ApiHelper {

    private CookieStore cookieStore = new BasicCookieStore();

    // Método genérico para realizar una solicitud GET
    private <R> R getResponseAsync(String username, String token, String url, Class<R> responseType)
            throws Exception {
        int responseCode;
        String responseBodyString = "";
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            //getBasicSession(username, token, httpclient);
            HttpGet httpGet = new HttpGet(url);
            //httpGet.setHeader("Cookie", createCookieHeader(cookieStore.getCookies()));
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                responseCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    responseBodyString = EntityUtils.toString(entity);
                }
            }
        } catch (IOException e) {
            throw new Exception("Error en la solicitud HTTP: " + e.getMessage());
        }

        return (R) responseBodyString;
    }
}










// Aquí necesitas convertir responseBodyString al tipo de respuesta deseado (R)
// En este ejemplo, asumimos que la respuesta es una cadena (String), pero podrías cambiarlo según tus necesidades.
// También puedes agregar un método que convierta la respuesta JSON en un objeto Java utilizando una biblioteca como Gson.
// Por ejemplo:
// R responseObject = gson.fromJson(responseBodyString, responseType);