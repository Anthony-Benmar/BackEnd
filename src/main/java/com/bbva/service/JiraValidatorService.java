package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.JiraValidatorByUrlResponse;

public class JiraValidatorService {
    //llamar al jira api service
    private final JiraApiService jiraApiService = new JiraApiService("username","token");

    //Todas la reglas de negocio
    public IDataResult<JiraValidatorByUrlResponse> getValidatorByUrl(JiraValidatorByUrlRequest dto) {
        jiraApiService.testConnection();
        JiraValidatorByUrlResponse response = new JiraValidatorByUrlResponse("OK");
        return new SuccessDataResult<>(response, "CONEXION EXITOSA");
    }
}
