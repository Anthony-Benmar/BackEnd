package com.bbva.crons;

import com.bbva.authentication.GoogleAuthentication;
import com.google.api.client.http.HttpStatusCodes;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class SampleCron extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        try {

            String urlCF = "https://us-central1-dev-bbva-gob-dicc-datos-pe-sp.cloudfunctions.net/fnMigration";
            var auth = GoogleAuthentication.makeGetRequest(urlCF, urlCF);

            HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

            HttpRequest requestFn = HttpRequest.newBuilder()
                    .uri(URI.create(urlCF))
                    .header("Content-Type", "application/json")
                    .header("Authorization", GoogleAuthentication.token)
                    .GET()
                    .build();

            HttpResponse respFn = httpClient.send(requestFn, HttpResponse.BodyHandlers.ofString());

            System.out.println(respFn.body());

            response.setStatus(HttpStatusCodes.STATUS_CODE_OK);
            response.getWriter().print(respFn.body().toString());

        } catch (IOException e) {
            response.setStatus(HttpStatusCodes.STATUS_CODE_SERVER_ERROR);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}