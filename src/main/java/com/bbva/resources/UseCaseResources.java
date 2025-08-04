package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.use_case.request.UpdateOrInsertUseCaseDtoRequest;
import com.bbva.dto.use_case.request.UseCaseInputsFilterDtoRequest;
import com.bbva.dto.use_case.response.UpdateOrInsertDtoResponse;
import com.bbva.dto.use_case.response.UseCaseInputsFilterDtoResponse;
import com.bbva.entities.use_case.UseCaseEntity;
import com.bbva.service.UseCaseService;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/use-cases")
@Produces(MediaType.APPLICATION_JSON)
public class UseCaseResources {
    private final UseCaseService useCaseService = new UseCaseService();
    private static final String CD = "Content-Disposition";

    @GET
    @Path("list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<UseCaseEntity>> listUseCases() {
        return useCaseService.listUseCases();
    }
    @PUT
    @Path("/update-or-insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateOrInsertUseCase(UpdateOrInsertUseCaseDtoRequest dto) {
        IDataResult<UpdateOrInsertDtoResponse> response = useCaseService.updateOrInsertUseCase(dto);

        if (Boolean.TRUE.equals(response.success)) {
            return Response.status(Response.Status.CREATED).entity(response).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
        }
    }

    @POST
    @Path("/get-filtered-use-cases")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<UseCaseInputsFilterDtoResponse> getFilteredUseCases(UseCaseInputsFilterDtoRequest dto) {
        return useCaseService.getFilteredUseCases(dto);
    }

    @POST
    @Path("/documentGenerator")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response downloadUseCasesExcel(UseCaseInputsFilterDtoRequest dto) {
        byte[] excel = useCaseService.generateDocumentUseCases(dto);

        String filename = "CasosDeUso_v1.xlsx";

        return Response
                .ok(excel)
                .header(CD, "attachment; filename=\"" + filename + "\"")
                .header("Access-Control-Expose-Headers", CD)
                .build();
    }

    @OPTIONS
    @Path("/documentGenerator")
    public Response options() {
        return Response.ok().build();
    }
}
