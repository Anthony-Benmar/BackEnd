package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.use_case.request.UpdateOrInsertUseCaseDtoRequest;
import com.bbva.dto.use_case.request.UseCaseInputsFilterDtoRequest;
import com.bbva.dto.use_case.response.UseCaseInputsFilterDtoResponse;
import com.bbva.entities.use_case.UseCaseEntity;
import com.bbva.service.UseCaseService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/use-cases")
@Produces(MediaType.APPLICATION_JSON)
public class UseCaseResources {
    private UseCaseService useCaseService = new UseCaseService();

    @GET
    @Path("list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<UseCaseEntity> listUseCases() throws IOException, InterruptedException {
        return useCaseService.listUseCases();
    }
    @PUT
    @Path("/update-or-insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateOrInsertUseCase(UpdateOrInsertUseCaseDtoRequest dto) {
        var response = useCaseService.updateOrInsertUseCase(dto);
        if(response.success) {
            return Response.ok().status(Response.Status.CREATED).entity(response).build();
        }
        return Response.ok().status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
    }
    @POST
    @Path("/get-filtered-use-cases")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<UseCaseInputsFilterDtoResponse> getFilteredUseCases(UseCaseInputsFilterDtoRequest dto)
            throws IOException, InterruptedException {
        return useCaseService.getFilteredUseCases(dto);
    }
}
