package com.bbva.resources;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.JiraResDTO;
import com.bbva.service.JiraValidatorService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/jira")
@Produces(MediaType.APPLICATION_JSON)
public class JiraValidatorResources {
    private JiraValidatorService jiraValidatorService = new JiraValidatorService();

    @POST
    @Path("/validator/validate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<JiraResDTO> validatorByUrl(JiraValidatorByUrlRequest dto)
            throws Exception {
        return jiraValidatorService.getValidatorByUrl(dto);
    }
}
