package com.bbva.authentication;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;

import java.io.IOException;

public class GoogleAuthentication {

    public static String token = null;

    public static HttpResponse makeGetRequest(String serviceUrl, String audience) throws IOException {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        if (!(credentials instanceof IdTokenProvider)) {
            throw new IllegalArgumentException("Credentials are not an instance of IdTokenProvider.");
        }
        IdTokenCredentials tokenCredential =
                IdTokenCredentials.newBuilder()
                        .setIdTokenProvider((IdTokenProvider) credentials)
                        .setTargetAudience(audience)
                        .build();

        GenericUrl genericUrl = new GenericUrl(serviceUrl);
        HttpCredentialsAdapter adapter = new HttpCredentialsAdapter(tokenCredential);
        HttpTransport transport = new NetHttpTransport();
        HttpRequest request = transport.createRequestFactory(adapter).buildGetRequest(genericUrl);

        token = request.getHeaders().getAuthorization();
        System.out.println(request.getHeaders().getAuthorization());
        return request.execute();
    }

}
