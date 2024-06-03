package com.bbva.resources;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.JiraValidatorByUrlResponse;
import com.bbva.service.JiraValidatorService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutionException;


@Path("/jira")
@Produces(MediaType.APPLICATION_JSON)
public class JiraValidatorResources {
    private JiraValidatorService jiraValidatorService = new JiraValidatorService();

    @POST
    @Path("/validator/validate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<JiraValidatorByUrlResponse> validatorByUrl(JiraValidatorByUrlRequest dto)
            throws ExecutionException, InterruptedException {
        return jiraValidatorService.getValidatorByUrl(dto);
    }
}
