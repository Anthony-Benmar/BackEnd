package com.bbva.config;

import com.google.cloud.secretmanager.v1.AccessSecretVersionRequest;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SecretManagerService {

        public static String getSecretValue(String secretName, String key) {
        try {
            String projectId = "bbva-gob-dicc-datos-pe-sp";

            SecretManagerServiceClient client = SecretManagerServiceClient.create();
            SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretName, "latest");

            AccessSecretVersionRequest request = AccessSecretVersionRequest.newBuilder()
                    .setName(secretVersionName.toString())
                    .build();

            String secretValue = client.accessSecretVersion(request).getPayload().getData().toStringUtf8();
            client.close();

            String value = null;
            if (secretValue != null && !secretValue.isEmpty()) {
                JsonObject jsonObject = JsonParser.parseString(secretValue).getAsJsonObject();
                if (jsonObject.has(key)) {
                    value = jsonObject.get(key).getAsString();
                }
            }

            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}