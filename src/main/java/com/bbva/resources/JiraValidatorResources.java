package com.bbva.resources;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.dao.JiraValidatorLogDao;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.JiraResponseDTO;
import com.bbva.service.JiraApiService;
import com.bbva.service.JiraValidatorService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/jira")
@Produces(MediaType.APPLICATION_JSON)
public class JiraValidatorResources {
    private final JiraApiService jiraApiService = new JiraApiService();
    private final JiraValidatorLogDao jiraValidatorLogDao = new JiraValidatorLogDao();
    private final JiraValidatorService jiraValidatorService = new JiraValidatorService(jiraApiService, jiraValidatorLogDao);

    @POST
    @Path("/validator/validate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<JiraResponseDTO> validatorByUrl(JiraValidatorByUrlRequest dto)
            throws Exception {
        return jiraValidatorService.getValidatorByUrl(dto);
    }
}
