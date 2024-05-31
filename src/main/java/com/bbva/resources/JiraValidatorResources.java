package com.bbva.resources;



import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.jira.response.JiraValidatorByUrlResponse;
import com.bbva.service.JiraValidatorService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutionException;


    @Path("/jira")
    @Produces(MediaType.APPLICATION_JSON)
    public class JiraValidatorResources {
        private JiraValidatorService jiraValidatorService = new JiraValidatorService();

        @POST
        @Path("/Validator/{jiraurl}")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public IDataResult<JiraValidatorByUrlResponse> ValidatorByUrl(@Context HttpServletRequest request, @PathParam("jiraurl") String jiraurl)

                throws ExecutionException, InterruptedException {
            return jiraValidatorService.getValidatorByUrl(jiraurl);
        }
    }
