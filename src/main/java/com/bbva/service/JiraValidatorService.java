package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.JiraValidatorByUrlResponse;

public class JiraValidatorService {
    private JiraApiService jiraApiService;

    //Todas la reglas de negocio
    public IDataResult<JiraValidatorByUrlResponse> getValidatorByUrl(JiraValidatorByUrlRequest dto) { //username, token
        jiraApiService = new JiraApiService(dto.getUserName(), dto.getToken());
        jiraApiService.testConnection();

        //REgla 1
        //Regla_1();
        //REgla 2


//        JiraValidatorByUrlResponse response = new JiraValidatorByUrlResponse("OK");
//        return new SuccessDataResult<>(response, "CONEXION EXITOSA");

        return null;
    }
    //MEtodos de las validaciones
    //void Regla_1(){
    // /Cuerpo
    //        }
}
