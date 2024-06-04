package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.JiraValidatorByUrlResponse;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JiraValidatorService {
    private JiraApiService jiraApiService;
    private boolean isValidURL;
    private Object jiraTicketResult;
    private String jiraCode;

    //Todas la reglas de negocio
    public IDataResult<JiraValidatorByUrlResponse> getValidatorByUrl(JiraValidatorByUrlRequest dto) throws Exception { //username, token
        jiraApiService = new JiraApiService(dto.getUserName(), dto.getToken());


        dto.setUrlJira(dto.getUrlJira().toUpperCase());
        validateJiraURL(dto.getUrlJira());
        jiraCode = dto.getUrlJira().split("/")[dto.getUrlJira().split("/").length - 1];


        if (!isValidURL) {
            System.out.println("CONEXION FALLIDA");
            return new SuccessDataResult<>(new JiraValidatorByUrlResponse("ERROR"), "CONEXION FALLIDA");
        } else {
            System.out.println("CONEXION EXITOSA");
            // Querying Jira API
            String queryResult = jiraApiService.searchByTicket(List.of(jiraCode),
                    List.of("id", "issuetype", "changelog", "teamId", "petitionerTeamId", "receptorTeamId", "labels", "featureLink", "issuelinks", "status", "summary", "acceptanceCriteria", "subtasks", "impactLabel", "itemType", "techStack",
                            "fixVersions", "attachment", "prs")).toString();
            System.out.println("QUERY RESULT: " + queryResult);
            String results = queryResult;
            System.out.println("RESULTS: " + results);
            if (results != null && !results.isEmpty()) {
                jiraTicketResult = results;
                System.out.println(jiraTicketResult);
            }
            return new SuccessDataResult<>(new JiraValidatorByUrlResponse("OK"), "CONEXION EXITOSA");
        }
    }
    public void validateJiraURL(String jiraURL) {
        String regexPattern = "^(?:https://jira.globaldevtools.bbva.com/(?:browse/)?(?:plugins/servlet/mobile#issue/)?)?([a-zA-Z0-9]+-[a-zA-Z0-9]+)$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(jiraURL.toLowerCase());
        this.isValidURL = matcher.matches();
    }
}
