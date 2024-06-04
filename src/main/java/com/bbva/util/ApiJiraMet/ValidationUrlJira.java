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
    private  String jiraCode ;
    private String jiraPADCode;
    private  String boxClassesBorder;
    private final String validPADList = "pad3,pad5";
    private final String ticketGroup = "Ticket";

    public ValidationUrlJira(String jiraCode) {
        this.jiraCode = jiraCode;
    }

    public  Map<String, Object> getValidationURLJIRA(String helpMessage, String group) {
        String message;
        boolean isValid;
        boolean isWarning = false;


        String[] jiraCodeParts = this.jiraCode.split("-");
        String jiraPADCode = jiraCodeParts[0].toUpperCase();

        if (this.validPADList.contains(jiraPADCode.toLowerCase())) {
            //message = "Se encontr&oacute; <div class=\"" + this.boxClassesBorder + "\">" + jiraPADCode + "</div>";
            message = "Se encontró " + jiraPADCode.toLowerCase();
            isValid = true;
        } else {
            //message = "No encontr&oacute;  <div class=\"" + this.boxClassesBorder + "\">" + String.join(" o ", this.validPADList) + "</div>";
            message = "No se encontró " + String.join(" o ", this.validPADList);
            isValid = false;
        }

        return Map.of("message", message, "isValid", isValid, "isWarning", isWarning, "helpMessage", helpMessage, "group", group);
    }

}
