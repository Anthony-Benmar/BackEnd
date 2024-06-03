package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.JiraValidatorByUrlResponse;
import com.sun.tools.jconsole.JConsoleContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JiraValidatorService {
    private JiraApiService jiraApiService;
    private boolean isValidURL;

    //Todas la reglas de negocio
    public IDataResult<JiraValidatorByUrlResponse> getValidatorByUrl(JiraValidatorByUrlRequest dto) { //username, token
        jiraApiService = new JiraApiService(dto.getUserName(), dto.getToken());
        jiraApiService.testConnection();

        dto.setUrlJira(dto.getUrlJira().toUpperCase());
        validateJiraURL(dto.getUrlJira());
        if (!isValidURL) {
            System.out.println("URL INVALIDA");
            return new SuccessDataResult<>(new JiraValidatorByUrlResponse("URL INVALIDA"), "URL INVALIDA");
        }else {
            System.out.println("CONEXION EXITOSA");
            return new SuccessDataResult<>(new JiraValidatorByUrlResponse("OK"), "CONEXION EXITOSA");
        }


        //REgla 1
        //Regla_1();
        //REgla 2


//        JiraValidatorByUrlResponse response = new JiraValidatorByUrlResponse("OK");
//        return new SuccessDataResult<>(response, "CONEXION EXITOSA");

    }

    public void validateJiraURL(String jiraURL) {
        String regexPattern = "^(?:https://jira.globaldevtools.bbva.com/(?:browse/)?(?:plugins/servlet/mobile#issue/)?)?([a-zA-Z0-9]+-[a-zA-Z0-9]+)$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(jiraURL.toLowerCase());
        this.isValidURL = matcher.matches();
    }

    //MEtodos de las validaciones
    //void Regla_1(){
    // /Cuerpo
    //        }
}
