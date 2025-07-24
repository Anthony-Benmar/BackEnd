package com.bbva.resources;
import com.bbva.core.HandledException;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.InfoJiraProjectDao;
import com.bbva.dao.JiraValidatorLogDao;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.DmJiraValidatorResponseDTO;
import com.bbva.dto.jira.response.JiraResponseDTO;
import com.bbva.service.DmJiraValidatorService;
import com.bbva.service.JiraApiService;
import com.bbva.service.JiraValidatorService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.ws.rs.*;
import java.util.List;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/jira")
@Produces(MediaType.APPLICATION_JSON)
public class JiraValidatorResources {
    private final JiraApiService jiraApiService = new JiraApiService();
    private final JiraValidatorLogDao jiraValidatorLogDao = new JiraValidatorLogDao();
    private final InfoJiraProjectDao infoJiraProjectDao = new InfoJiraProjectDao();
    private final JiraValidatorService jiraValidatorService = new JiraValidatorService(jiraApiService, jiraValidatorLogDao, infoJiraProjectDao);
    private final DmJiraValidatorService dmJiraValidatorService = new DmJiraValidatorService();

    @POST
    @Path("/validator/validate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<JiraResponseDTO> validatorByUrl(JiraValidatorByUrlRequest dto)
            throws Exception {
        return jiraValidatorService.getValidatorByUrl(dto);
    }

    @POST
    @Path("/validator/dm")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<DmJiraValidatorResponseDTO> validatorDataModelling(JiraValidatorByUrlRequest dto)
            throws Exception {

        var messages = dmJiraValidatorService.validateHistoriaDM(dto);

        DmJiraValidatorResponseDTO response = new DmJiraValidatorResponseDTO();
        response.setData(messages);
        response.setSuccessCount((int) messages.stream().filter(m -> m.getStatus().equals("success")).count());
        response.setErrorCount((int) messages.stream().filter(m -> m.getStatus().equals("error")).count());
        response.setWarningCount((int) messages.stream().filter(m -> m.getStatus().equals("warning")).count());

        return new SuccessDataResult<>(response, "Validación Data Modelling ejecutada");
    }
}