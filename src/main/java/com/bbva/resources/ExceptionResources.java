package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.batch.request.BatchIssuesActionFilterDtoRequest;
import com.bbva.dto.batch.response.BatchIssuesActionFilterDtoResponse;
import com.bbva.dto.batch.response.ExceptionEntityResponseDTO;
import com.bbva.dto.batch.response.StatusJobExecutionDTO;
import com.bbva.service.ExceptionService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/exception")
@Produces(MediaType.APPLICATION_JSON)
public class ExceptionResources {
    private ExceptionService exceptionService = new ExceptionService();
    @GET
    @Path("/getExceptionsWithSource")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<ExceptionEntityResponseDTO>> exceptionService() {
        IDataResult<List<ExceptionEntityResponseDTO>> result = exceptionService.getExceptionsWithSource();
        return result;
    }

}
