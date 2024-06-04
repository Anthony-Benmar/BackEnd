package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.JiraValidatorByUrlResponse;
import com.sun.tools.jconsole.JConsoleContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JiraValidatorService {
    private JiraApiService jiraApiService;
    private boolean isValidURL;
    private Object jiraTicketResult;
    private String jiraCode;

    //Todas la reglas de negocio
    public IDataResult<JiraValidatorByUrlResponse> getValidatorByUrl(JiraValidatorByUrlRequest dto) { //username, token
        jiraApiService = new JiraApiService(dto.getUserName(), dto.getToken());
        jiraApiService.testConnection();

        dto.setUrlJira(dto.getUrlJira().toUpperCase());
        validateJiraURL(dto.getUrlJira());
        jiraCode = dto.getUrlJira().split("/")[dto.getUrlJira().split("/").length - 1];
        if (!isValidURL) {

            System.out.println("CONEXION FALLIDA");
            return new SuccessDataResult<>(new JiraValidatorByUrlResponse("ERROR"), "CONEXION FALLIDA");

        } else {
            System.out.println("CONEXION EXITOSA");
            // Querying Jira API
            List<Map<String, Object>> queryResult = jiraApiService.searchByTicket(List.of(jiraCode),
                    List.of("id", "issuetype", "changelog", "teamId", "petitionerTeamId", "receptorTeamId", "labels", "featureLink", "issuelinks", "status", "summary", "acceptanceCriteria", "subtasks", "impactLabel", "itemType", "techStack",
                            "fixVersions", "attachment", "prs"));
            System.out.println("QUERY RESULT: " + queryResult);
            List<Map<String, Object>> results = queryResult;
            System.out.println("RESULTS: " + results);
            if (results != null && !results.isEmpty()) {
                jiraTicketResult = results.get(0);
                System.out.println(jiraTicketResult);
                //List<Map<String, Object>> attachments = (List<Map<String, Object>>) jiraTicketResult.get("attachment");
//                if (attachments != null) {
//                    for (Map<String, Object> attachment : attachments) {
//                        adjuntos.add((String) attachment.get("filename"));
//                    }
//                }
//                extraTicketResults = __getExtraTicketResults(jiraTicketResult);
//                __detectParentOIssuesTicketType(extraTicketResults.get("parentIssueLinksDeployedTablero05Develop"));
//                featureLinkTicket = (Map<String, Object>) extraTicketResults.get("featureLink");
//                dependencyTicket = (Map<String, Object>) extraTicketResults.get("dependency");
//                issueType = (String) jiraTicketResult.get("issuetype.name");
//                currentTeamFieldLabel = teamFieldLabelByIssueType.containsKey(issueType) ? teamFieldLabelByIssueType.get(issueType).get("label") : "";
//                currentTeamFieldField = teamFieldLabelByIssueType.containsKey(issueType) ? teamFieldLabelByIssueType.get(issueType).get("field") : "";
                //jiraTicketStatus = (String) jiraTicketResult.get("status.name");
            }
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
